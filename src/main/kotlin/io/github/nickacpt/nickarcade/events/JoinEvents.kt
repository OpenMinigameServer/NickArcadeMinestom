package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.chat.ChatChannelsManager
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.events.impl.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import io.github.nickacpt.nickarcade.utils.interop.launch
import io.github.nickacpt.nickarcade.utils.interop.name
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.*
import java.util.*

fun registerJoinEvents() {
    MinecraftServer.getConnectionManager().setUuidProvider { playerConnection, username ->
        return@setUuidProvider runBlocking {
            runCatching { ProfileApi.getProfileByName(username)?.uniqueId }.getOrNull() ?: UUID.randomUUID()
        }
    }

    event<PlayerSkinInitEvent>(forceBlocking = true) {
        val profile = ProfileApi.getProfileByName(this.player.name) ?: return@event
//
//        val properties: List<ProfileProperty> = profile.toPlayerProfile().properties
//        if (properties.isNotEmpty()) {
//            val (_, value1, signature) = properties.stream().findFirst().get()
//            skin = PlayerSkin(value1, signature)
//        }
        player.skin = profile.toPlayerProfile().toSkin()
    }
    blockInvalidNames()

    event<PlayerLoginEvent> {
        sendPlayerDataActionBar()

        val playerData = async {
            player.getPlayerData()
        }

        callEvent(PlayerDataJoinEvent(playerData))
        callEvent(PlayerDataReloadEvent(playerData))
    }



    cancelEvent<PlayerChatEvent>
    {
        player.skin = pluginInstance.walterProfile(player.uniqueId, player.name).toSkin()
        val playerData = this.player.getPlayerData()
        val channel = ChatChannelsManager.getChannelByType(playerData.currentChannel)
        channel.sendMessageInternal(playerData, this.message, ChatMessageOrigin.CHAT)
    }
}


private fun blockInvalidNames() {
    val validPattern = Regex("^[a-zA-Z0-9_]{3,16}\$")
    event<AsyncPlayerPreLoginEvent> {
        val isValidName = validPattern.matchEntire(this.username) != null

        if (!isValidName) {
            player.kick("You are using an invalid Minecraft name and thus you got denied access.")
        } else if (PlayerDataManager.isPlayerDataLoaded(this.playerUuid)) {
            player.kick("Please wait while we save your data to join again.")
        }
    }
}

private fun PlayerLoginEvent.sendPlayerDataActionBar() {
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
    event<PlayerDisconnectEvent> {
        handleLeave(player.uniqueId)
    }
}

private suspend fun handleLeave(playerId: UUID) {
    val data = MinecraftServer.getConnectionManager().getPlayer(playerId)?.getPlayerData() ?: return
    PlayerDataLeaveEvent(data).callEvent()
    PlayerDataManager.saveAndRemovePlayerData(playerId)
}
