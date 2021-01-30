package io.github.nickacpt.nickarcade.game.impl

import io.github.nickacpt.nickarcade.game.Game
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import net.minestom.server.instance.Instance
import java.util.*

class BedWarsGame(id: UUID, miniGame: BaseMiniGame, arenaDefinition: ArenaDefinition, arena: Instance) : Game(
    id, miniGame,
    arenaDefinition,
    arena
) {
}