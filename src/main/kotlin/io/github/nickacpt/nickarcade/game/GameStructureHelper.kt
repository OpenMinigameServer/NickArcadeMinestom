package io.github.nickacpt.nickarcade.game

import dev.sejtam.mineschem.core.schematic.SpongeSchematic
import io.github.nickacpt.nickarcade.SchematicSizeHelper
import io.github.nickacpt.nickarcade.game.definition.position.GamePosition
import io.github.nickacpt.nickarcade.schematics.SchematicInstance
import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import io.github.nickacpt.nickarcade.schematics.manager.SchematicName
import io.github.nickacpt.nickarcade.utils.buildCentered

object GameStructureHelper {

    fun createWaitingLobby(instance: SchematicInstance): GamePosition {
        val lobbySchematic = SpongeSchematic(
            SchematicManager.getSchematicFileByName(SchematicName.LOBBY),
            instance.instance
        ).also { it.read() }
        lobbySchematic as SchematicSizeHelper

        val schematic = instance.schematic
        schematic as SchematicSizeHelper

        val originalY = instance.finalYPosition + schematic.height + 1
        lobbySchematic.buildCentered(0f, originalY, 0f, instance.instance)

        return GamePosition(0f, originalY + (lobbySchematic.height / 2), 0f)
    }

}