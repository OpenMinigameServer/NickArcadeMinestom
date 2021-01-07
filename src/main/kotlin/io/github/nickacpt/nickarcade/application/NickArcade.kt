package io.github.nickacpt.nickarcade.application

import net.minestom.server.MinecraftServer
import net.minestom.server.extras.PlacementRules
import net.minestom.server.extras.selfmodification.MinestomRootClassLoader

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


    server.start(
        "0.0.0.0", 25565
    ) { connection, responseData ->
        responseData.apply {
            responseData.setDescription("NickArcade")
        }
    }
}
