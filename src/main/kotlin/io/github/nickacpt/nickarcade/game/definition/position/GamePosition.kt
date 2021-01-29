package io.github.nickacpt.nickarcade.game.definition.position

import net.minestom.server.utils.Position as MinestomPosition

data class GamePosition(val x: Double, val y: Double, val z: Double) {
    fun toMinestom(): MinestomPosition {
        return MinestomPosition(x, y, z)
    }
}
