package io.github.nickacpt.nickarcade.events.impl

import io.github.nickacpt.nickarcade.data.player.PlayerData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerDataJoinEvent(val player: PlayerData, val isProfileReload: Boolean = false) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}