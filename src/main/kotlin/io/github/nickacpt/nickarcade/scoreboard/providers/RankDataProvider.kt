package io.github.nickacpt.nickarcade.scoreboard.providers

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.scoreboard.IScoreboardDataProvider

object RankDataProvider : IScoreboardDataProvider {
    override fun providePrefix(player: ArcadePlayer): String {
        return player.effectivePrefix
    }
}