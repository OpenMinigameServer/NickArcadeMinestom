package io.github.nickacpt.nickarcade.application

import net.minestom.server.Bootstrap

fun main(args: Array<String>) {
    // allow to load mixins without using an extension, nor enforcing launch arguments
    val argsWithMixins = arrayOfNulls<String>(args.size + 2)
    System.arraycopy(args, 0, argsWithMixins, 0, args.size)
    argsWithMixins[argsWithMixins.size - 2] = "--mixin"
    argsWithMixins[argsWithMixins.size - 1] = "mixins.nickarcade.json"

    System.setProperty("minestom.extension.indevfolder.classes", "../build/classes/java")
    System.setProperty("minestom.extension.indevfolder.resources", "../build/resources/main/")

    Bootstrap.bootstrap("io.github.nickacpt.nickarcade.application.NickArcadeKt", argsWithMixins)
}