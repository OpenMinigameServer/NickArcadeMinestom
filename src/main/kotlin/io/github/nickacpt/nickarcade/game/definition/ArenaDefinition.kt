package io.github.nickacpt.nickarcade.game.definition

import io.github.nickacpt.nickarcade.game.definition.position.GamePosition

data class ArenaDefinition(
    val name: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val schematicId: String,
    val baseYPosition: Float,
    val spawnPosition: GamePosition
)
