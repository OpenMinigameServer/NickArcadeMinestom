package io.github.nickacpt.nickarcade.game.impl

import io.github.nickacpt.nickarcade.game.Game
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import io.github.nickacpt.nickarcade.game.definition.MiniGameMode
import io.github.nickacpt.nickarcade.game.definition.MiniGameType
import net.minestom.server.instance.Instance
import java.util.*

object BedWarsMiniGame : BaseMiniGame() {
    override val type: MiniGameType
        get() = MiniGameType.BED_WARS

    override fun provideGameInstance(
        gameId: UUID,
        miniGame: BaseMiniGame,
        mode: MiniGameMode,
        arenaDefinition: ArenaDefinition,
        arena: Instance
    ): Game {
        return BedWarsGame(gameId, miniGame, mode, arenaDefinition, arena)
    }
}