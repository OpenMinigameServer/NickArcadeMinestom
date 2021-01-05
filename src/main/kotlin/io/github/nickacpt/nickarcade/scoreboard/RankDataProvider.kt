package io.github.nickacpt.nickarcade.scoreboard

import io.github.nickacpt.nickarcade.data.player.PlayerData

object RankDataProvider : IScoreboardDataProvider {
    override fun provideData(player: PlayerData): ScoreboardData {
        return ScoreboardData(player.effectivePrefix)
    }
}