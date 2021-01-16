package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor.*
import io.github.nickacpt.hypixelapi.utis.profile.Profile
import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.refreshPlayerTeams
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.removePlayerInfo
import io.github.nickacpt.nickarcade.utils.interop.PlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.ProfileProperty
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission

fun registerPlayerDataEvents() {

    event<PlayerDataLeaveEvent> {
        val data = player
        val player = data.player ?: return@event

        //Cleanup party disguise profile
        if (data.displayOverrides.isPartyDisguise) {
            data.displayOverrides.displayProfile = null
            data.displayOverrides.isPartyDisguise = false
        }

        if (data.currentChannel.isInternal) {
            data.currentChannel = ChatChannelType.ALL
        }
        player.actualPlayerProfile = ProfileApi.getProfileService().findById(player.uniqueId)?.toPlayerProfile()

        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            it.removePlayerInfo(data.displayName)
        }
    }

    event<PlayerDataJoinEvent> {
        showLobbyMessage()
    }

    event<PlayerDataReloadEvent> {
        val minestomPlayer = player.player ?: return@event
        minestomPlayer.setDisplayProfile(player.displayOverrides.displayProfile)
        player.displayOverrides.isProfileOverridden = true
        setupPermissions(player, minestomPlayer)
        refreshPlayerTeams()
    }
}

fun Profile.toPlayerProfile(): PlayerProfile {
    return PlayerProfile(uuid, name).also { bukkitProfile ->
        val raw = this.textures?.raw
        if (raw != null) {
            bukkitProfile.properties.add(ProfileProperty("textures", raw.value, raw.signature))
        }
        bukkitProfile.name = this.name
    }
}

fun setupPermissions(player: ArcadePlayer, minestomPlayer: Player) {
    HypixelPackageRank.values().forEach {
        if (player.hasAtLeastRank(it, true)) {
            minestomPlayer.addPermission(Permission(it.name.toLowerCase()))
        }
        if (player.hasAtLeastRank(HypixelPackageRank.ADMIN))
            minestomPlayer.addPermission(Permission("worldedit.*"))
    }
}

private fun PlayerDataJoinEvent.showLobbyMessage() {
    val superStarColors = listOf(BLUE, RED, GREEN)
    val joinPrefix =
        if (player.hasAtLeastDisplayRank(HypixelPackageRank.SUPERSTAR)) " ${superStarColors.joinToString("") { "$it>" }} " else ""
    val joinSuffix = if (player.hasAtLeastDisplayRank(HypixelPackageRank.SUPERSTAR)) " ${
        superStarColors.reversed().joinToString("") { "$it<" }
    } " else ""
    if (player.hasAtLeastDisplayRank(HypixelPackageRank.MVP_PLUS)) {
        minestomAudiences.all().sendMessage(
            Component.text("$joinPrefix${player.getChatName()}§6 joined the lobby!$joinSuffix")
                .hoverEvent(player.computeHoverEventComponent())
        )
    }
}
