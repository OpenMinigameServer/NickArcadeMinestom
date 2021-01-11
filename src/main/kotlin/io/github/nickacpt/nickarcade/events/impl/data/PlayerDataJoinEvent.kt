package io.github.nickacpt.nickarcade.events.impl.data

import io.github.nickacpt.nickarcade.data.player.PlayerData
import net.minestom.server.event.Event

class PlayerDataJoinEvent(val player: PlayerData, val isProfileReload: Boolean = false) : Event() {
}