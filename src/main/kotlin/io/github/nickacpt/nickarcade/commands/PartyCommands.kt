package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.kotlin.MutableCommandBuilder
import cloud.commandframework.kotlin.extension.buildAndRegister
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.party.model.Party
import io.github.nickacpt.nickarcade.party.model.PartySetting
import io.github.nickacpt.nickarcade.party.model.PartySettings
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.NickArcadeCommandManager
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

object PartyCommands {

    fun registerPartySettings(manager: NickArcadeCommandManager<CommandSender>) {
        val partySettingsKClass = PartySettings::class
        val properties = partySettingsKClass.memberProperties.filter { it.hasAnnotation<PartySetting>() }
        properties.forEach { prop ->
            if (prop !is KMutableProperty1<PartySettings, *>) return@forEach
            val setting = prop.findAnnotation<PartySetting>() ?: return@forEach

            manager.buildAndRegister("party", aliases = arrayOf("p")) {
                literal("settings", aliases = arrayOf("setting"))
                literal(setting.aliases.first(), aliases = setting.aliases.drop(1).toTypedArray())
                createPartyPropSubcommand(prop, setting)
            }
        }
    }

    private fun MutableCommandBuilder<CommandSender>.createPartyPropSubcommand(
        prop: KProperty1<PartySettings, *>,
        setting: PartySetting
    ) {
        senderType(Player::class.java)
        val isToggle = prop.returnType.jvmErasure == Boolean::class
        if (!isToggle) {
            val returnType = prop.returnType.jvmErasure.java
            this.argument(CommandArgument.ofType(returnType, "value"))
        }

        handler {
            val partyProp = prop as KMutableProperty1<PartySettings, Any?>

            val sender = it.sender as? Player ?: return@handler
            command(sender, setting.requiredRank) {
                val player = sender.getPlayerData()
                val party = player.getCurrentParty(true) ?: return@command
                if (!party.isLeader(player)) {
                    player.audience.sendMessage(separator {
                        append(text("You are not the party leader!", NamedTextColor.RED))
                    })
                    return@command
                }

                if (isToggle) {
                    val newValue = (partyProp.get(party.settings) as Boolean).not()
                    notifyToggleChanged(party, player, setting, newValue)
                    partyProp.set(party.settings, newValue)
                } else {
                    partyProp.set(party.settings, it["value"])
                }
            }
        }
    }

    private fun notifyToggleChanged(party: Party, player: PlayerData, setting: PartySetting, newValue: Boolean) {
        val toggleMessage = if (newValue) "enabled" else "disabled"
        val colour = if (newValue) NamedTextColor.GREEN else NamedTextColor.RED

        party.audience.sendMessage(
            separator {
                append(text(player.getChatName(true)))
                append(text(" has $toggleMessage ${setting.description}", colour))
            }
        )
    }

    @CommandMethod("party|p <target>")
    fun invitePlayerShort(senderPlayer: Player, @Argument("target") target: PlayerData) =
        invitePlayer(senderPlayer, target)

    @CommandMethod("pl")
    fun listPartyShort(senderPlayer: Player) =
        listParty(senderPlayer)

    @CommandMethod("pl <target>")
    fun listPartyOtherShort(senderPlayer: Player, @Argument("target") target: PlayerData) =
        listPartyOther(senderPlayer, target)

    @CommandMethod("party|p invite <player>")
    fun invitePlayer(
        senderPlayer: Player,
        @Argument("player")
        target: PlayerData
    ) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.getOrCreateParty()

        if (sender == target) {
            sender.audience.sendMessage(separator {
                append(text("You cannot party yourself!", NamedTextColor.RED))
            })
            return@command
        }
        party.invitePlayer(sender, target)
    }

    @CommandMethod("party|p leave")
    fun partyLeavePlayer(
        senderPlayer: Player
    ) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.getCurrentParty(true) ?: return@command

        party.removeMember(sender, true)
    }

    @CommandMethod("party|p disband")
    fun disbandParty(senderPlayer: Player) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.getCurrentParty() ?: return@command

        party.audience.sendMessage(separator {
            append(text(sender.getChatName(true)))
            append(text(" has disbanded the party!", NamedTextColor.YELLOW))
        })

        party.disband()
    }

    @CommandMethod("party|p hijack <target>")
    fun hijackParty(senderPlayer: Player, @Argument("target") target: PlayerData) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = target.getCurrentParty() ?: return@command

        party.audience.sendMessage(separator {
            append(text(sender.getChatName(true)))
            append(text(" has hijacked the party!", NamedTextColor.YELLOW))
        })

        party.switchOwner(sender)
    }

    @CommandMethod("party|p accept <target>")
    fun acceptParty(senderPlayer: Player, @Argument("target") target: PlayerData) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = target.getCurrentParty()

        if (party == null || !party.hasPendingInvite(sender)) {
            sender.audience.sendMessage(separator {
                append(text("That party has been disbanded.", NamedTextColor.RED))
            })
            return@command
        }

        party.pendingInvites.remove(sender)
        party.addMember(sender, true)
    }

    @CommandMethod("party|p kick <target>")
    fun kickParty(senderPlayer: Player, @Argument("target") target: PlayerData) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.getCurrentParty(true) ?: return@command
        if (!party.isLeader(sender)) {
            sender.audience.sendMessage(separator {
                append(text("You are not the party leader.", NamedTextColor.RED))
            })
            return@command
        }

        party.removeMember(target, true, true)

    }

    @CommandMethod("party|p list")
    fun listParty(senderPlayer: Player) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.getCurrentParty(true) ?: return@command

        sender.audience.sendMessage(party.listMessage)
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("party|p list <target>")
    fun listPartyOther(senderPlayer: Player, @Argument("target") target: PlayerData) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = target.getCurrentParty() ?: run {
            sender.audience.sendMessage(separator {
                append(text(target.getChatName(true)))
                append(text(" is not in a party right now.", NamedTextColor.RED))
            })
            return@command
        }

        sender.audience.sendMessage(party.listMessage)
    }
}