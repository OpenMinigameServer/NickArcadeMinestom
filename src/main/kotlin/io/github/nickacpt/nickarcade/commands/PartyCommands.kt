package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.data.getPlayerData
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

object PartyCommands {

    @CommandMethod("party|p <target>")
    fun invitePlayerShort(senderPlayer: Player, @Argument("target") target: PlayerData) =
        invitePlayer(senderPlayer, target)

    @CommandMethod("party|p disband")
    fun disbandPlayer(senderPlayer: Player) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.currentParty

        if (party == null) {
            sender.audience.sendMessage(separator {
                append(text("You are not in a party right now.", NamedTextColor.RED))
            })
            return@command
        }
        party.audience.sendMessage(separator {
            append(text(sender.getChatName(true)))
            append(text(" has disbanded the party!", NamedTextColor.YELLOW))
        })

        sender.currentParty = null
    }

    @CommandMethod("party|p invite <player>")
    fun invitePlayer(
        senderPlayer: Player,
        @Argument("player")
        target: PlayerData
    ) = command(senderPlayer) {
        val sender = senderPlayer.getPlayerData()
        val party = sender.getOrCreateParty()

        /*if (sender == target) {
            sender.audience.sendMessage(separator {
                append(text("You cannot party yourself!", NamedTextColor.RED))
            })
            return@command
        }*/
        party.invitePlayer(sender, target)


    }

}