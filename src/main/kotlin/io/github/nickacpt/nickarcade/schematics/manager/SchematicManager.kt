package io.github.nickacpt.nickarcade.schematics.manager

import io.github.nickacpt.nickarcade.schematics.SchematicInstance
import io.github.nickacpt.nickarcade.utils.pluginInstance
import java.io.File

object SchematicManager {
    private val dataFolder by lazy {
        File(
            pluginInstance.dataFolder,
            "schematics"
        ).also { it.mkdirs() }
    }

    fun getSchematicInstance(name: SchematicName, yPosition: Float): SchematicInstance? {
        val schematic = getSchematicFileByName(name).takeIf { it.exists() } ?: return null
        return SchematicInstance(schematic, yPosition)
    }

    fun getSchematicInstance(name: String, yPosition: Float): SchematicInstance? {
        val schematic = getSchematicFileByName(name).takeIf { it.exists() } ?: return null
        return SchematicInstance(schematic, yPosition)
    }

    fun getSchematicFileByName(name: SchematicName): File = getSchematicFileByName(name.name.toLowerCase())

    fun getSchematicFileByName(name: String): File {
        return File(dataFolder, "$name.schem")
    }

    fun hasSchematicByName(name: SchematicName): Boolean = hasSchematicByName(name.name.toLowerCase())

    fun hasSchematicByName(name: String): Boolean {
        return getSchematicFileByName(name).exists()
    }

}
