package io.github.nickacpt.nickarcade.events

import com.github.shynixn.mccoroutine.launch
import io.github.nickacpt.nickarcade.chat.ChatChannelsManager
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.events.impl.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.*
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.event.player.*
import java.util.*

fun registerJoinEvents() {
    blockInvalidNames()

    event<PlayerJoinEvent> {
        joinMessage = null

        sendPlayerDataActionBar()

        val playerData = async {
            player.getPlayerData()
        }

        Bukkit.getPluginManager().callEvent(PlayerDataJoinEvent(playerData))
        Bukkit.getPluginManager().callEvent(PlayerDataReloadEvent(playerData))
    }



    event<AsyncPlayerChatEvent>
    {
        isCancelled = true
        val playerData = this.player.getPlayerData()
        val channel = ChatChannelsManager.getChannelByType(playerData.currentChannel)
        channel.sendMessageInternal(playerData, this.message)
    }
}


private fun blockInvalidNames() {
    val validPattern = Regex("^[a-zA-Z0-9_]{3,16}\$")
    event<AsyncPlayerPreLoginEvent> {
        val isValidName = validPattern.matchEntire(this.name) != null

        if (!isValidName) {
            kickMessage = "You are using an invalid Minecraft name and thus you got denied access."
            loginResult = AsyncPlayerPreLoginEvent.Result.KICK_BANNED
        }
    }
}

private fun PlayerJoinEvent.sendPlayerDataActionBar() {
    pluginInstance.launch {
        val audience = player.asAudience
        while (!PlayerDataManager.isPlayerDataLoaded(player.uniqueId)) {
            audience.sendActionBar(
                text(
                    "Fetching player data from Hypixel, please wait!",
                    NamedTextColor.RED,
                    TextDecoration.BOLD
                )
            )
            delay(5.ticks)
        }
        audience.sendActionBar(text("Player data fetched from Hypixel! Have a nice stay.", NamedTextColor.GREEN))
    }
}

fun registerLeaveEvents() {
    event<PlayerQuitEvent> {
        handleLeave(player.uniqueId)
    }
    event<PlayerKickEvent> {
        handleLeave(player.uniqueId)
    }
}

private suspend fun handleLeave(playerId: UUID) {
    val data = Bukkit.getPlayer(playerId)?.getPlayerData() ?: return
    PlayerDataLeaveEvent(data).callEvent()
    PlayerDataManager.saveAndRemovePlayerData(playerId)
}