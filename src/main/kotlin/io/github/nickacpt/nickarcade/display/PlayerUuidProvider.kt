package io.github.nickacpt.nickarcade.display

import net.minestom.server.entity.Player
import java.util.*

interface PlayerUuidProvider {
    suspend fun getPlayerUuid(player: Player): UUID?
}