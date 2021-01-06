package io.github.nickacpt.nickarcade.events.impl

import io.github.nickacpt.nickarcade.data.player.PlayerData
import net.minestom.server.event.Event

class PlayerDataReloadEvent(val player: PlayerData) : Event() {
}