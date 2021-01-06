package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor.*
import io.github.nickacpt.hypixelapi.utis.profile.Profile
import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.events.impl.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.party.model.PartyHelper
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.removePlayerInfo
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.setPlayerInfo
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.setupOwnScoreboard
import io.github.nickacpt.nickarcade.utils.interop.PlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.ProfileProperty
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission

fun registerPlayerDataEvents() {

    event<PlayerDataJoinEvent> {
        val data = player

        //Restore current party
        data.currentParty = PartyHelper.getCachedParty(player.uuid)

        //Also restore player instance because PlayerData is a difference object now
        data.currentParty?.restorePlayer(data)
    }

    event<PlayerDataLeaveEvent> {
        val data = player
        val player = data.player ?: return@event

        player.actualPlayerProfile = ProfileApi.getProfileService().findById(player.uniqueId)?.toPlayerProfile()

        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            it.removePlayerInfo(data.displayName)
        }
    }

    event<PlayerDataReloadEvent> {

        val bukkitPlayer = player.player ?: return@event
        bukkitPlayer.setDisplayProfile(player.displayOverrides.displayProfile)
        setupPermissions(player, bukkitPlayer)
        bukkitPlayer.setupOwnScoreboard()

        updateNewPlayerTeamForOnlinePlayers(bukkitPlayer)

        showLobbyMessage()
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

fun setupPermissions(player: PlayerData, bukkitPlayer: Player) {
    val minestomPlayer = player.player ?: return
    HypixelPackageRank.values().forEach {
        if (player.hasAtLeastRank(it)) {
            minestomPlayer.addPermission(Permission(it.name.toLowerCase()))
        }
    }
}

private fun PlayerDataReloadEvent.showLobbyMessage() {
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

private suspend fun updateNewPlayerTeamForOnlinePlayers(joinedPlayer: Player) {
    MinecraftServer.getConnectionManager().onlinePlayers.forEach {
        it.setPlayerInfo(joinedPlayer)
    }
}
