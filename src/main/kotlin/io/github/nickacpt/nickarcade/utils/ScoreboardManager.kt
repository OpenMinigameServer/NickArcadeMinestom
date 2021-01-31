package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.nickarcade.data.player.ExtraDataTag
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.scoreboard.ScoreboardData
import io.github.nickacpt.nickarcade.scoreboard.ScoreboardDataProviderManager
import io.github.nickacpt.nickarcade.scoreboard.SidebarData
import io.github.nickacpt.nickarcade.utils.interop.getLastColors
import io.github.nickacpt.nickarcade.utils.interop.toNative
import net.kyori.adventure.platform.minestom.MinestomComponentSerializer
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatColor
import net.minestom.server.entity.Player
import net.minestom.server.scoreboard.Sidebar
import net.minestom.server.scoreboard.Team


object ScoreboardManager {

    private val sidebarTag = ExtraDataTag.of<ArcadeSidebar>("sidebar")

    suspend fun refreshPlayerTeams() {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {
            refreshScoreboard(it)
        }
    }

    suspend fun refreshScoreboard(player: Player) {
        val data = player.getArcadeSender()

        val playerName = data.displayName
        val teamManager = MinecraftServer.getTeamManager()

        teamManager.deleteTeam(playerName)
        val team = teamManager.createTeam(playerName)

        val scoreData = ScoreboardDataProviderManager.computeData(data)
        applyScoreboardTeam(team, scoreData, playerName)
        val sidebar = data[sidebarTag] ?: ArcadeSidebar(playerName)
        scoreData.sideBar.also { applySidebarData(sidebar, player, it) }
        data[sidebarTag] = sidebar
    }

    private fun applySidebarData(sidebar: ArcadeSidebar, player: Player, data: SidebarData?) {
        if (data != null) {
            sidebar.addViewer(player)
        } else {
            sidebar.removeViewer(player)
            return
        }

        val serializer = MinestomComponentSerializer.get()
        val legacySerializer = LegacyComponentSerializer.legacySection()
        val title = legacySerializer.serialize(data.title)
        sidebar.setTitle(title)

        val lines = data.lines
        val linesSize = lines.size
        lines.forEachIndexed { index, component ->
            val number = linesSize - index
            val lineId = number.toString()

            val line = sidebar.getLine(lineId)
            val content = serializer.serialize(component.colorIfAbsent(NamedTextColor.WHITE))

            if (line != null) {
                sidebar.updateLineContent(lineId, content)
            } else {
                sidebar.createLine(Sidebar.ScoreboardLine(lineId, content, number))
            }
        }
    }

    private fun applyScoreboardTeam(
        team: Team,
        scoreData: ScoreboardData,
        playerName: String
    ) {
        team.prefix = text(scoreData.prefix ?: "").toNative()
        team.suffix = text(scoreData.suffix?.trimEnd()?.let { " $it" } ?: "").toNative()

        //Compute team color
        getLastColors(scoreData.prefix ?: "").replace("§", "").firstOrNull()?.let { message ->
            ChatColor.fromLegacyColorCodes(message).let {
                team.teamColor = it
            }
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