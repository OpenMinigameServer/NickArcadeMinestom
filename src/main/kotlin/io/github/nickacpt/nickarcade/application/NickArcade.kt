package io.github.nickacpt.nickarcade.application

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.PlacementRules
import net.minestom.server.extras.selfmodification.MinestomRootClassLoader
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.rule.vanilla.WallPlacementRule
import net.minestom.server.utils.Position
import net.minestom.server.world.biomes.Biome
import java.util.*

fun main(args: Array<String>) {
    MinestomRootClassLoader.getInstance().protectedPackages.addAll(
        arrayOf(
            "org.reactivestreams",
            "io.leangen.geantyref",
            "kotlinx"
        )
    )

    val server = MinecraftServer.init()
//    MojangAuth.init()
    PlacementRules.init()
    MinecraftServer.getBlockManager().registerBlockPlacementRule(WallPlacementRule(Block.CRIMSON_BUTTON))
    val instanceManager = MinecraftServer.getInstanceManager()

    // Create the instance
    val instanceContainer = instanceManager.createInstanceContainer()
    // Set the ChunkGenerator
    instanceContainer.chunkGenerator = object : ChunkGenerator {
        override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
            for (x in 0 until Chunk.CHUNK_SIZE_X) for (z in 0 until Chunk.CHUNK_SIZE_Z) {
                for (y in 0..54) {
                    val block = when {
                        (chunkX + chunkZ) % 2 == 0 -> Block.RED_SAND
                        else -> Block.SAND
                    }
                    batch.setBlock(x, y, z, block)
                }
            }
        }

        override fun fillBiomes(biomes: Array<out Biome>, chunkX: Int, chunkZ: Int) = Arrays.fill(biomes, Biome.PLAINS);

        override fun getPopulators(): MutableList<ChunkPopulator>? = null

    }
    // Enable the auto chunk loading (when players come close)
    instanceContainer.enableAutoChunkLoad(true)

    // Add an event callback to specify the spawning instance (and the spawn position)
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addEventCallback(
        PlayerLoginEvent::class.java
    ) { event: PlayerLoginEvent ->
        val player = event.player

        player.isAllowFlying = true
        player.gameMode = GameMode.CREATIVE

        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Position(0f, 55.0f, 0f)
    }

    server.start(
        "0.0.0.0", 25565
    ) { connection, responseData ->
        responseData.apply {
            responseData.setDescription("NickArcade")
        }
    }
}
