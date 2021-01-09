package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player

object PartyCommands {

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