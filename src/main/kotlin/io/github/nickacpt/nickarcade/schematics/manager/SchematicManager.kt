package io.github.nickacpt.nickarcade.schematics.manager

import com.sk89q.worldedit.extent.Extent
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import io.github.nickacpt.nickarcade.schematics.ReadOnlyClipboard
import io.github.nickacpt.nickarcade.utils.pluginInstance
import io.github.openminigameserver.worldedit.platform.chunkloader.ExtentChunkLoader
import net.minestom.server.MinecraftServer
import net.minestom.server.data.DataImpl
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceContainer
import java.io.File

val Instance.extent: Extent?
    get() = (((this as? InstanceContainer)?.chunkLoader as? ExtentChunkLoader)?.extent)

val Instance.clipboard: Clipboard?
    get() = this.extent as? Clipboard

object SchematicManager {

    private val dataFolder by lazy {
        File(
            pluginInstance.dataFolder,
            "schematics"
        ).also { it.mkdirs() }
    }

    private val clipboardCache = mutableMapOf<String, Clipboard>()
    fun getClipboard(name: SchematicName): Clipboard? = getClipboard(name.name.toLowerCase())

    fun getClipboard(name: String): Clipboard? {
        clipboardCache[name]?.let { return it }

        val file = File(dataFolder, "$name.schem").takeIf { it.exists() } ?: return null
        val format =
            ClipboardFormats.findByFile(file) ?: throw Exception("Schematic file '$name' is not a valid schematic")

        return format.getReader(file.inputStream()).read()?.also { clipboardCache[name] = it }
    }

    fun getInstanceForSchematic(name: SchematicName): Instance? = getInstanceForSchematic(name.name.toLowerCase())

    fun getInstanceForSchematic(name: String): Instance? {
        val clipboard = getClipboard(name) ?: return null
        return MinecraftServer.getInstanceManager().createInstanceContainer().apply {
            timeRate = 0
            time = -6000
            data = DataImpl()
            enableAutoChunkLoad(true)
            chunkLoader = ExtentChunkLoader(ReadOnlyClipboard(clipboard))
        }
    }
}
