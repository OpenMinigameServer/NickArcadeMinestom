package io.github.nickacpt.nickarcade.schematics

import dev.sejtam.mineschem.core.schematic.ISchematic
import dev.sejtam.mineschem.core.schematic.SpongeSchematic
import io.github.nickacpt.nickarcade.SchematicSizeHelper
import io.github.nickacpt.nickarcade.utils.EmptyChunkGenerator
import io.github.nickacpt.nickarcade.utils.buildCentered
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import java.io.File

private fun createEmptyInstance() = MinecraftServer.getInstanceManager().createInstanceContainer().apply {
    // Set the ChunkGenerator
    chunkGenerator = EmptyChunkGenerator()
    // Enable the auto chunk loading (when players come close)
    enableAutoChunkLoad(true)
}

class SchematicInstance(
    val schematic: ISchematic,
    val yPosition: Float,
    val instance: Instance = createEmptyInstance()
) {

    constructor(schematic: File, yPosition: Float, instance: Instance = createEmptyInstance()) : this(
        SpongeSchematic(
            schematic,
            instance
        ), yPosition, instance
    )

    val finalYPosition by lazy { yPosition - ((schematic as SchematicSizeHelper).height / 2f) }
    init {
        schematic.read()

        schematic.buildCentered(
            0f,
            finalYPosition,
            0f,
            instance
        )
    }

}