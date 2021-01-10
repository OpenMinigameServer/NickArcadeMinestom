package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.nickarcade.chat.ChatEmote
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.utils.asAudience
import io.github.nickacpt.nickarcade.utils.command
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player

object MiscCommands {
    @CommandMethod("emotes")
    fun emotesCommand(sender: CommandSender) = command(sender) {
        val asAudience = sender.asAudience
        asAudience.sendMessage(text {
            it.append(text("Available to ", GREEN))
            it.append(text("MVP", GOLD))
            it.append(text("++", RED))
            it.append(text(":", GREEN))
        })

        ChatEmote.values().forEach { emote ->
            asAudience.sendMessage(text {
                it.append(text(emote.emote, GOLD))
                it.append(text(" - ", WHITE))
                it.append(emote.replacement)
            })
        }
    }

    @CommandMethod("lobby|l")
    fun lobbyCommand(sender: Player) = command(sender) {
        val player = sender.getPlayerData()

        val currentGame = player.getCurrentGame()
        if (currentGame != null) {
            currentGame.let { MiniGameManager.removePlayer(it, player) }
        } else {
            player.audience.sendMessage(text("You are already on a lobby!", RED))
        }

    }
}