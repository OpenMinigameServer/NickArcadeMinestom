package io.github.nickacpt.nickarcade.scoreboard

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer

interface IScoreboardDataProvider {
    fun providePrefix(player: ArcadePlayer): String? = null

    fun provideSuffix(player: ArcadePlayer): String? = null
}