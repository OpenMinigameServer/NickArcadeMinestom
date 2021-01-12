package io.github.nickacpt.nickarcade.game.definition

import io.github.nickacpt.nickarcade.game.Game
import io.github.nickacpt.nickarcade.game.GameStructureHelper
import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import java.util.*

abstract class BaseMiniGame {
    abstract val type: MiniGameType

    protected inline fun <reified T> gameEvent(noinline handler: suspend T.(Game) -> Unit) {
        TODO("GameEvent not implemented yet!")
    }

    fun createGame(arenaDefinition: ArenaDefinition): Game {
        val arena = SchematicManager.getSchematicInstance(arenaDefinition.schematicId, arenaDefinition.baseYPosition)
            ?: throw Exception("Unable to find arena with schematic id ${arenaDefinition.schematicId}")

        val spawnPosition = GameStructureHelper.createWaitingLobby(arena)
        return Game(UUID.randomUUID(), this, arenaDefinition.copy(spawnPosition = spawnPosition), arena)
    }
}