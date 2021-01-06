package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.scoreboard.ScoreboardDataProviderManager
import io.github.nickacpt.nickarcade.utils.interop.getLastColors
import io.github.nickacpt.nickarcade.utils.interop.toNative
import net.kyori.adventure.text.Component.text
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.entity.Player


object ScoreboardManager {

    suspend fun Player.setupOwnScoreboard() {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            val joinedPlayer = it
            setPlayerInfo(this, joinedPlayer)
        }
    }

    suspend fun setPlayerInfo(receiver: Player, joinedPlayer: Player) {
        val data = joinedPlayer.getPlayerData()

        val playerName = data.displayName
        val teamManager = MinecraftServer.getTeamManager()

        val team = teamManager.getTeam(playerName) ?: teamManager.createTeam(playerName)
        val scoreData = ScoreboardDataProviderManager.computeData(data)
        team.prefix = text(scoreData.prefix ?: "").toNative()
        team.suffix = text(scoreData.suffix ?: "").toNative()

        //Compute team color
        ChatColor.fromLegacyColorCodes(getLastColors(scoreData.prefix ?: "").replace("§", "").first()).let {
            team.teamColor = it
        }

        if (team.members.contains(playerName)) {
            team.removeMember(playerName)
        }
        team.addMember(playerName)

        team.sendUpdatePacket()
    }

    fun Player.removePlayerInfo(quitPlayer: String) {
        MinecraftServer.getTeamManager().deleteTeam(quitPlayer)
    }

}