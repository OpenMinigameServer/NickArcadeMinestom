package io.github.nickacpt.nickarcade.game.impl

import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import io.github.nickacpt.nickarcade.game.definition.MiniGameType

object BedWarsMiniGame : BaseMiniGame() {
    init {

    }

    override val type: MiniGameType
        get() = MiniGameType.BED_WARS
}