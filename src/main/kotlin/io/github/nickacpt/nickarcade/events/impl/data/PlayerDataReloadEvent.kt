package io.github.nickacpt.nickarcade.events.impl.data

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import net.minestom.server.event.Event

class PlayerDataReloadEvent(val player: ArcadePlayer) : Event()