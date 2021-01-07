package io.github.nickacpt.nickarcade.schematics

import dev.sejtam.mineschem.core.schematic.ISchematic
import dev.sejtam.mineschem.core.schematic.SpongeSchematic
import io.github.nickacpt.nickarcade.SchematicSizeHelper
import io.github.nickacpt.nickarcade.utils.EmptyChunkGenerator
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position
import net.minestom.server.utils.chunk.ChunkUtils
import java.io.File

private fun createEmptyInstance() = MinecraftServer.getInstanceManager().createInstanceContainer().apply {
    // Set the ChunkGenerator
    chunkGenerator = EmptyChunkGenerator()
    // Enable the auto chunk loading (when players come close)
    enableAutoChunkLoad(true)
}

class SchematicInstance(
    schematic: ISchematic,
    yPosition: Float,
    val instance: Instance = createEmptyInstance()
) {

    constructor(schematic: File, yPosition: Float, instance: Instance = createEmptyInstance()) : this(
        SpongeSchematic(
            schematic,
            instance
        ), yPosition, instance
    )

    init {
        val schematicSizeHelper = schematic as SchematicSizeHelper
        schematic.read()

        val x = -(schematicSizeHelper.width / 2f)
        val z = -(schematicSizeHelper.length / 2f)

        for (xValue in x.toInt()..(x + schematic.width).toInt()) {
            for (zValue in z.toInt()..(z + schematic.length).toInt()) {

                instance.loadChunk(ChunkUtils.getChunkCoordinate(xValue), ChunkUtils.getChunkCoordinate(zValue))
            }
        }

        schematic.build(
            Position(
                x,
                yPosition - (schematicSizeHelper.height / 2f),
                z
            )
        )
    }
}