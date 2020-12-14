package io.github.nickacpt.nickarcade.data

import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.utils.pluginInstance
import org.bukkit.entity.Player
import org.litote.kmongo.upsert
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlayerDataManager {
    private val loadedPlayerMap = ConcurrentHashMap<UUID, PlayerData>()

    fun isPlayerDataLoaded(id: UUID): Boolean {
        val impersonation = ImpersonationManager.getImpersonation(id)
        if (impersonation != null)
            return loadedPlayerMap.containsKey(impersonation.uniqueId)

        return loadedPlayerMap.containsKey(id)
    }

    private suspend fun createPlayerDataFromHypixel(id: UUID, name: String): PlayerData {
        return PlayerData(id, fetchHypixelPlayerData(id, name))
    }

    private suspend fun fetchHypixelPlayerData(id: UUID, name: String): HypixelPlayer {
        val tryGetPlayerById = pluginInstance.hypixelPlayerInfoHelper.tryGetPlayerById(id)
        return if (tryGetPlayerById != null) {
            pluginInstance.logger.info("Fetched Hypixel Player Data for $name [$id] successfully.")
            tryGetPlayerById
        } else {
            pluginInstance.logger.info("Unable to fetch Hypixel Player Data for $name [$id].")
            HypixelPlayer(
                name.toLowerCase(),
                name,
                networkExp = 0.0
            )
        }
    }

    suspend fun getPlayerData(uniqueId: UUID, name: String): PlayerData {
        return if (loadedPlayerMap[uniqueId] != null) {
            loadedPlayerMap[uniqueId]!!
        } else {
            pluginInstance.logger.info("Unable to find cached player data for $name [$uniqueId]. Fetching from MongoDb or Hypixel.")
            val playerData = playerDataCollection.findOneById(uniqueId) ?: createPlayerDataFromHypixel(uniqueId, name)
            playerData.also {
                loadedPlayerMap[uniqueId] = it
            }
        }
    }

    val playerDataCollection by lazy {
        pluginInstance.database.getCollection<PlayerData>("players")
    }

    suspend fun saveAndRemovePlayerData(uuid: UUID) {
        val id = ImpersonationManager.getImpersonation(uuid)?.uniqueId ?: uuid
        loadedPlayerMap[id]?.also {
            savePlayerData(it)
            loadedPlayerMap.remove(id)
        }
    }

    suspend fun savePlayerData(it: PlayerData) {
        pluginInstance.logger.info("Saving player data for ${it.displayName} [${it.uuid}]")
        playerDataCollection.updateOneById(it.uuid, it, upsert())
    }
}

suspend fun Player.getPlayerData(): PlayerData {
    val impersonation = ImpersonationManager.getImpersonation(uniqueId)
    if (impersonation != null)
        return PlayerDataManager.getPlayerData(impersonation.uniqueId, impersonation.name)

    return PlayerDataManager.getPlayerData(uniqueId, name)
}
