package io.github.nickacpt.nickarcade.scoreboard.providers

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.scoreboard.IScoreboardDataProvider

object LegacyPlayerProvider : IScoreboardDataProvider {
    override fun provideSuffix(player: ArcadePlayer): String? {
        return player.takeIf { (it.displayOverrides.overrides?.isLegacyPlayer ?: it.overrides.isLegacyPlayer) == true }
            ?.let { "§3[LEGACY]" }
    }
}