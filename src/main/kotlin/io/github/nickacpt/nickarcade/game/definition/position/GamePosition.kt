package io.github.nickacpt.nickarcade.game.definition.position

import net.minestom.server.utils.Position as MinestomPosition

data class GamePosition(val x: Float, val y: Float, val z: Float) {
    fun toMinestom(): MinestomPosition {
        return MinestomPosition(x, y, z)
    }
}
