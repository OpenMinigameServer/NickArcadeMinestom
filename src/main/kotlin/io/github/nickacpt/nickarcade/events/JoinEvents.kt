package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.chat.ChatChannelsManager
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.invite.InviteManager
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import io.github.nickacpt.nickarcade.utils.interop.name
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.platform.minestom.MinestomComponentSerializer
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.player.*
import java.util.*

fun registerJoinEvents() {
    MinecraftServer.getConnectionManager().setPlayerProvider { uuid, username, connection ->
        Player(uuid, username, connection)
    }
    registerOfflineModeOnlineIds()
    registerPreLoginEvent()

    cancelEvent<PlayerChatEvent>
    {
//        player.skin = pluginInstance.walterProfile(player.uniqueId, player.name).toSkin()
        val playerData = this.player.getArcadeSender()
        val channel = ChatChannelsManager.getChannelByType(playerData.currentChannel)
        channel.sendMessageInternal(playerData, this.message, ChatMessageOrigin.CHAT)
    }
}

private fun registerOfflineModeOnlineIds() {
    MinecraftServer.getConnectionManager().setUuidProvider { _, username ->
        return@setUuidProvider runBlocking {
            runCatching { ProfileApi.getProfileByName(username)?.uniqueId }.getOrNull() ?: UUID.nameUUIDFromBytes(
                username.toByteArray()
            )
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
}


val validNamePattern = Regex("^[a-zA-Z0-9_]{3,16}\$")
private fun registerPreLoginEvent() {
    event<AsyncPlayerPreLoginEvent> {
        if (!InviteManager.hasPlayerReceivedInvite(playerUuid)) {
            player.kick(MinestomComponentSerializer.get().serialize(text {
                it.append(text("You are not allowed to join this server!", RED).append(newline()))
                it.append(newline())
                it.append(
                    text("Reason: ", GRAY).append(
                        text(
                            "You have not received an invite to play on this server.",
                            WHITE
                        )
                    ).append(newline())
                )
            }))
            return@event
        }

        val isValidName = validNamePattern.matchEntire(this.username) != null

        if (!isValidName) {
            player.kick("You are using an invalid Minecraft name and thus you got denied access.")
        } /*else if (PlayerDataManager.isPlayerDataLoaded(this.playerUuid)) {
            PlayerDataManager.saveAndRemovePlayerData(this.playerUuid)
            player.kick("Please wait while we save your data to join again.")
        }*/
    }
}


fun registerLeaveEvents() {
    event<PlayerDisconnectEvent> {
        handleLeave(player.uniqueId)
    }
}

private suspend fun handleLeave(playerId: UUID) {
    val data = MinecraftServer.getConnectionManager().getPlayer(playerId)?.getArcadeSender() ?: return
    PlayerDataLeaveEvent(data).callEvent()
    PlayerDataManager.saveAndRemovePlayerData(playerId)
}
