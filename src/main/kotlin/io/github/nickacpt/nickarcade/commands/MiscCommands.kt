package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.nickarcade.NickArcadeExtension
import io.github.nickacpt.nickarcade.chat.ChatEmote
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.utils.command
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*

object MiscCommands {
    @CommandMethod("apistats|apistatus")
    fun apiStatusCommand(sender: ArcadeSender) = command(sender) {
        val info = NickArcadeExtension.instance.service.getKeyInformation()
        sender.audience.sendMessage(text {
            it.append(text("Made ", GOLD))
            it.append(text(info.record!!.queriesInPastMin + 1, GREEN))
            it.append(text(" out of ", GOLD))
            it.append(text(info.record!!.limit, GREEN))
            it.append(text(" queries to Hypixel in the last minute.", GOLD))
        })
    }

    @CommandMethod("emotes")
    fun emotesCommand(sender: ArcadeSender) = command(sender) {
        val asAudience = sender.audience
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
    fun lobbyCommand(sender: ArcadePlayer) = command(sender) {

        val currentGame = sender.getCurrentGame()
        if (currentGame != null) {
            currentGame.let { MiniGameManager.removePlayer(it, sender) }
        } else {
            sender.audience.sendMessage(text("You are already on a lobby!", RED))
        }
    }
}