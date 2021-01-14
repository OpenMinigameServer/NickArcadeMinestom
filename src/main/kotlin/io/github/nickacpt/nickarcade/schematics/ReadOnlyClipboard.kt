package io.github.nickacpt.nickarcade.schematics

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.biome.BiomeType
import com.sk89q.worldedit.world.block.BlockStateHolder

class ReadOnlyClipboard(private val clipboard: Clipboard) : Clipboard by clipboard {
    override fun <T : BlockStateHolder<T>?> setBlock(position: BlockVector3?, block: T): Boolean {
        return true
    }

    override fun setBiome(position: BlockVector2?, biome: BiomeType?): Boolean {
        return true
    }

    override fun setBiome(position: BlockVector3?, biome: BiomeType?): Boolean {
        return true
    }

    override fun commit(): Operation? {
        return null
    }
}