package io.github.nickacpt.nickarcade.game.definition

import io.github.nickacpt.nickarcade.game.Game
import io.github.nickacpt.nickarcade.game.GameStructureHelper
import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import net.minestom.server.instance.Instance
import java.util.*
import kotlin.time.Duration
import kotlin.time.seconds

abstract class BaseMiniGame {
    abstract val type: MiniGameType

    val lobbyWaitTime: Duration
        get() = 15.seconds

    fun createGame(arenaDefinition: ArenaDefinition, mode: MiniGameMode): Game? {
        val arena = SchematicManager.getInstanceForSchematic(arenaDefinition.schematicId)
            ?: throw Exception("Unable to find arena with schematic id ${arenaDefinition.schematicId}")

        val spawnPosition = GameStructureHelper.createWaitingLobby(arena) ?: return null
        return provideGameInstance(
            UUID.randomUUID(),
            this,
            mode,
            arenaDefinition.copy(spawnPosition = spawnPosition),
            arena
        )
    }

    abstract fun provideGameInstance(
        gameId: UUID,
        miniGame: BaseMiniGame,
        mode: MiniGameMode,
        arenaDefinition: ArenaDefinition,
        arena: Instance
    ): Game
}