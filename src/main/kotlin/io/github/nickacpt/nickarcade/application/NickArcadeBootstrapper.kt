package io.github.nickacpt.nickarcade.application

import net.minestom.server.Bootstrap

fun main(args: Array<String>) {
    // allow to load mixins without using an extension, nor enforcing launch arguments
    val argsWithMixins = arrayOfNulls<String>(args.size + 4)
    System.arraycopy(args, 0, argsWithMixins, 0, args.size)
    argsWithMixins[argsWithMixins.size - 4] = "--mixin"
    argsWithMixins[argsWithMixins.size - 3] = "mixins.nickarcade.json"
    argsWithMixins[argsWithMixins.size - 2] = "--mixin"
    argsWithMixins[argsWithMixins.size - 1] = "mixins.replay.json"

    System.setProperty("minestom.extension.indevfolder.classes", "../build/classes/java")
    System.setProperty("minestom.extension.indevfolder.resources", "../build/resources/main/")

    Bootstrap.bootstrap("io.github.nickacpt.nickarcade.application.NickArcadeKt", argsWithMixins)
}