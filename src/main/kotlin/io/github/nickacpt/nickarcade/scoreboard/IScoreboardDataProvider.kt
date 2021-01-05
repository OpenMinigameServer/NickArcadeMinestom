package io.github.nickacpt.nickarcade.scoreboard

import io.github.nickacpt.nickarcade.data.player.PlayerData

fun interface IScoreboardDataProvider {
    fun provideData(player: PlayerData): ScoreboardData?
}