package io.github.nickacpt.nickarcade.application

import net.minestom.server.MinecraftServer
import net.minestom.server.extras.PlacementRules
import net.minestom.server.extras.optifine.OptifineSupport
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
//    MinecraftServer.setShouldProcessNettyErrors(true)
//    MojangAuth.init()
    println("Offline Auth enabled")
    OptifineSupport.enable()
    PlacementRules.init()
    MinecraftServer.setGroupedPacket(false)

    server.start(
        "0.0.0.0", 25566
    ) { _, responseData ->
        responseData.apply {
            responseData.setDescription("NickArcade")
        }
    }
}
