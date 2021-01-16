package io.github.nickacpt.nickarcade.scoreboard

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.scoreboard.providers.GameLobbyDataProvider
import io.github.nickacpt.nickarcade.scoreboard.providers.LegacyPlayerProvider
import io.github.nickacpt.nickarcade.scoreboard.providers.RankDataProvider

object ScoreboardDataProviderManager {

    private val providers = mutableListOf(
        LegacyPlayerProvider,
        GameLobbyDataProvider,
        RankDataProvider
    )

    fun registerProvider(provider: IScoreboardDataProvider) {
        providers.add(0, provider)
    }

    fun computeData(player: ArcadePlayer): ScoreboardData {
        val prefix = providers.mapNotNull { it.providePrefix(player) }.firstOrNull()
        val suffix = providers.mapNotNull { it.provideSuffix(player) }.firstOrNull()
        return ScoreboardData(prefix, suffix)
    }
}