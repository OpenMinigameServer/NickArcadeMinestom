package io.github.nickacpt.nickarcade.events.impl.game

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.game.Game
import net.minestom.server.event.Event

data class PlayerLeaveGameEvent(val game: Game, val player: ArcadePlayer) : Event()