package io.github.nickacpt.nickarcade.scoreboard.providers

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.scoreboard.IScoreboardDataProvider
import io.github.nickacpt.nickarcade.scoreboard.SidebarData
import io.github.nickacpt.nickarcade.utils.interop.getLastColors
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration


object GameLobbyDataProvider : IScoreboardDataProvider {
    override fun provideSideBar(player: ArcadePlayer): SidebarData? {
        return player.getCurrentGame()?.let { game ->
            val waitTimer = game.lobbyWaitTimer
            SidebarData(
                text(game.miniGame.type.friendlyName.toUpperCase(), Style.style(TextDecoration.BOLD)),
                mutableListOf<Component>().apply {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    add(text("${now.monthNumber.toString().padStart(2, '0')}/${now.dayOfMonth}/${now.year}", GRAY))
                    add(empty())
                    addAll(game.computeScoreboard(player))
                    add(empty())
                    add(text("NickArcade", YELLOW))
                }.toTypedArray()
            )
        }
    }

    override fun providePrefix(player: ArcadePlayer): String? {
        return player.getCurrentGame()?.let { getLastColors(player.effectivePrefix) }
    }
}