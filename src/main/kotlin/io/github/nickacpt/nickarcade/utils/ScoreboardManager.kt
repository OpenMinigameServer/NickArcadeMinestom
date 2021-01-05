package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.scoreboard.ScoreboardDataProviderManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ScoreboardManager {

    suspend fun Player.setupOwnScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().newScoreboard

        Bukkit.getOnlinePlayers().forEach {
            val joinedPlayer = it
            setPlayerInfo(joinedPlayer)
        }
    }

    suspend fun Player.setPlayerInfo(joinedPlayer: Player) {
        val data = joinedPlayer.getPlayerData()

        val playerName = data.displayName
        val team = scoreboard.getTeam(playerName) ?: scoreboard.registerNewTeam(playerName)
        val scoreData = ScoreboardDataProviderManager.computeData(data)
        team.prefix = scoreData.prefix ?: ""
        team.suffix = scoreData.suffix ?: ""

        //Compute team color
        ChatColor.getByChar(ChatColor.getLastColors(team.prefix).replace("§", ""))?.let {
            team.color = it
        }

        team.removeEntry(playerName)
        team.addEntry(playerName)

        this.scoreboard = scoreboard
    }

    fun Player.removePlayerInfo(quitPlayer: String) {
        scoreboard.getTeam(quitPlayer)?.unregister()
    }

}