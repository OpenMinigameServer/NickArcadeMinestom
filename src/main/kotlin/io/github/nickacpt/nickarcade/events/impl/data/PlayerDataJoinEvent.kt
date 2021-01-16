package io.github.nickacpt.nickarcade.events.impl.data

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import net.minestom.server.event.Event

class PlayerDataJoinEvent(val player: ArcadePlayer, val isProfileReload: Boolean = false) : Event() {
}