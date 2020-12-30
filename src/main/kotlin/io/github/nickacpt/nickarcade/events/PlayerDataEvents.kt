package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.events.impl.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.removePlayerInfo
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.setPlayerInfo
import io.github.nickacpt.nickarcade.utils.ScoreboardManager.setupOwnScoreboard
import io.github.nickacpt.nickarcade.utils.actualPlayerProfile
import io.github.nickacpt.nickarcade.utils.bukkitAudiences
import io.github.nickacpt.nickarcade.utils.event
import io.github.nickacpt.nickarcade.utils.pluginInstance
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

fun registerPlayerDataEvents() {
    event<PlayerDataLeaveEvent> {
        val data = player
        val player = data.player ?: return@event

        player.actualPlayerProfile = null

        Bukkit.getOnlinePlayers().forEach {
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

fun setupPermissions(player: PlayerData, bukkitPlayer: Player) {
    player.permission?.let { bukkitPlayer.removeAttachment(it) }
    val permissionAttachment = bukkitPlayer.addAttachment(pluginInstance)
    player.permission = permissionAttachment.apply {
        HypixelPackageRank.values().forEach {
            if (player.hasAtLeastRank(it)) {
                this.setPermission(it.name.toLowerCase(), true)
            }
        }
    }
}

private fun PlayerDataReloadEvent.showLobbyMessage() {
    val superStarColors = listOf(ChatColor.AQUA, ChatColor.RED, ChatColor.GREEN)
    val joinPrefix =
        if (player.hasAtLeastDisplayRank(HypixelPackageRank.SUPERSTAR)) " ${superStarColors.joinToString("") { "$it>" }} " else ""
    val joinSuffix = if (player.hasAtLeastDisplayRank(HypixelPackageRank.SUPERSTAR)) " ${
        superStarColors.reversed().joinToString("") { "$it<" }
    } " else ""
    if (player.hasAtLeastDisplayRank(HypixelPackageRank.MVP_PLUS)) {
        bukkitAudiences.all().sendMessage(
            Component.text("$joinPrefix${player.getChatName()}§6 joined the lobby!$joinSuffix")
                .hoverEvent(player.computeHoverEventComponent())
        )
    }
}

private suspend fun updateNewPlayerTeamForOnlinePlayers(joinedPlayer: Player) {
    Bukkit.getOnlinePlayers().forEach {
        it.setPlayerInfo(joinedPlayer)
    }
}
