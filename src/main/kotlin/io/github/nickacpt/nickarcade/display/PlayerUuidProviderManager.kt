package io.github.nickacpt.nickarcade.display

import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.interop.name
import kotlinx.coroutines.runBlocking
import net.minestom.server.entity.Player
import java.util.*


object PlayerUuidProviderManager {

    private val providers = mutableListOf<PlayerUuidProvider>()

    init {
        addProvider {
            val playerData = getPlayerData()
            val displayOverrides = playerData.displayOverrides
            val displayProfile = displayOverrides.displayProfile
            return@addProvider displayProfile?.uuid
        }

        addProvider {
            uuid
        }
    }

    private inline fun addProvider(crossinline code: suspend (Player).() -> UUID?) {
        providers += object : PlayerUuidProvider {
            override suspend fun getPlayerUuid(player: Player): UUID? {
                return code(player)
            }
        }
    }

    private suspend fun getPlayerUuidSuspend(player: Player): UUID {
        var id: UUID = UUID.randomUUID()
        providers.forEach { provider ->
            provider.getPlayerUuid(player)?.let {
                id = it
                println("Returning ${id} as the UUID for ${player.name}")
                return id
            }
        }

        println("Returning ${id} as the UUID for ${player.name}")
        return id
    }

    fun getPlayerUuid(player: Player): UUID {
        return runBlocking { getPlayerUuidSuspend(player) }
    }

}