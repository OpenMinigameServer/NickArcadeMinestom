package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.nickarcade.events.impl.game.PlayerJoinGameEvent
import io.github.nickacpt.nickarcade.utils.event
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

fun registerGameEvents() {
    event<PlayerJoinGameEvent> {
        game.audience.sendMessage(text {
            it.append(text(player.getChatName(colourPrefixOnly = true)))
            it.append(text(" has joined (", NamedTextColor.YELLOW))
            it.append(text(game.playerCount, NamedTextColor.AQUA))
            it.append(text("/", NamedTextColor.YELLOW))
            it.append(text(game.maxPlayerCount, NamedTextColor.AQUA))
            it.append(text(")!", NamedTextColor.YELLOW))
        })
    }

}