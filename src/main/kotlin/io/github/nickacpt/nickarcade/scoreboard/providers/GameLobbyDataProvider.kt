package io.github.nickacpt.nickarcade.scoreboard.providers

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.scoreboard.IScoreboardDataProvider
import io.github.nickacpt.nickarcade.utils.interop.getLastColors

object GameLobbyDataProvider : IScoreboardDataProvider {
    override fun providePrefix(player: ArcadePlayer): String? {
        return player.getCurrentGame()?.let { getLastColors(player.effectivePrefix) }
    }
}