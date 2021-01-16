package io.github.nickacpt.nickarcade.testflows.impl

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer

interface TestFlowImplementation {
    suspend fun execute(launcher: ArcadePlayer)
}