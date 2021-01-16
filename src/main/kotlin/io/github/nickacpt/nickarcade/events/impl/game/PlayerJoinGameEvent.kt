package io.github.nickacpt.nickarcade.events.impl.game

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.game.Game
import net.minestom.server.event.Event

data class PlayerJoinGameEvent(val game: Game, val player: ArcadePlayer, val playerCount: Int) : Event()

