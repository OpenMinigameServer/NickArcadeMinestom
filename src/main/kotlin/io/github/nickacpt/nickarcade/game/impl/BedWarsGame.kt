package io.github.nickacpt.nickarcade.game.impl

import io.github.nickacpt.nickarcade.game.Game
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import io.github.nickacpt.nickarcade.game.definition.MiniGameMode
import net.minestom.server.instance.Instance
import java.util.*

class BedWarsGame(
    id: UUID,
    miniGame: BaseMiniGame,
    mode: MiniGameMode,
    arenaDefinition: ArenaDefinition,
    arena: Instance
) : Game(
    id, miniGame,
    mode,
    arenaDefinition,
    arena
)