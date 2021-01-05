package io.github.nickacpt.nickarcade.scoreboard

import io.github.nickacpt.nickarcade.data.player.PlayerData

object ScoreboardDataProviderManager {

    private val providers = mutableListOf<IScoreboardDataProvider>(RankDataProvider)

    fun registerProvider(provider: IScoreboardDataProvider) {
        providers.add(0, provider)
    }

    fun computeData(player: PlayerData): ScoreboardData {
        return providers.mapNotNull { it.provideData(player) }.firstOrNull() ?: ScoreboardData()
    }
}