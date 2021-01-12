package io.github.nickacpt.nickarcade.utils

import dev.sejtam.mineschem.core.schematic.ISchematic
import io.github.nickacpt.nickarcade.SchematicSizeHelper
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import net.minestom.server.utils.chunk.ChunkUtils

fun ISchematic.buildCentered(
    x: Float,
    y: Float,
    z: Float,
    instance: Instance,
) {
    this as SchematicSizeHelper

    val finalX = -(width / 2f) + x
    val finalZ = -(length / 2f) + z
    for (xValue in finalX.toInt()..(finalX + width).toInt()) {
        for (zValue in finalZ.toInt()..(finalZ + length).toInt()) {
            instance.loadChunk(ChunkUtils.getChunkCoordinate(xValue), ChunkUtils.getChunkCoordinate(zValue))
        }
    }

    build(
        Position(
            finalX,
            y,
            finalZ
        )
    )
}
