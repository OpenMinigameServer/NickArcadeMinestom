package io.github.nickacpt.nickarcade.game.impl

import io.github.nickacpt.nickarcade.game.definition.MiniGameMode

enum class BedWarsMode(private val userFriendlyName: String) : MiniGameMode {
    SOLO("Solo");

    override val friendlyName: String
        get() = userFriendlyName
}