package io.github.nickacpt.nickarcade.testflows

import io.github.nickacpt.nickarcade.testflows.impl.JoinBedWarsGameImpl
import io.github.nickacpt.nickarcade.testflows.impl.TestFlowImplementation

enum class TestFlow(val implementation: TestFlowImplementation) {
    JOIN_BEDWARS_GAME(JoinBedWarsGameImpl)
}