package io.github.nickacpt.nickarcade.data.player

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.utils.interop.logger
import io.github.nickacpt.nickarcade.utils.interop.name
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import io.github.nickacpt.nickarcade.utils.pluginInstance
import io.github.nickacpt.nickarcade.utils.profiles.reloadProfile
import kotlinx.datetime.Clock
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import org.litote.kmongo.upsert
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.hours
import kotlin.time.measureTimedValue

object PlayerDataManager {
    private val loadedPlayerMap = ConcurrentHashMap<UUID, ArcadePlayer>()

    fun isPlayerDataLoaded(id: UUID): Boolean {
        val impersonation = ImpersonationManager.getImpersonation(id)
        if (impersonation != null)
            return loadedPlayerMap.containsKey(impersonation.uniqueId)

        return loadedPlayerMap.containsKey(id)
    }

    private suspend fun createPlayerDataFromHypixel(id: UUID, name: String): ArcadePlayer {
        return ArcadePlayer(id, fetchHypixelPlayerData(id, name)).also {
            if (it.effectiveRank >= HypixelPackageRank.HELPER) {
                it.overrides.rankOverride = HypixelPackageRank.MVP_PLUS
            }
        }
    }

    private suspend fun fetchHypixelPlayerData(id: UUID, name: String): HypixelPlayer {
        val tryGetPlayerById = pluginInstance.hypixelPlayerInfoHelper.tryGetPlayerById(id)

        return if (tryGetPlayerById != null) {
            logger.info("Fetched Hypixel Player Data for $name [$id] successfully.")
            tryGetPlayerById
        } else {
            logger.info("Unable to fetch Hypixel Player Data for $name [$id].")
            HypixelPlayer(
                name.toLowerCase(),
                name,
                networkExp = 0.0
            )
        }
    }

    suspend fun getPlayerData(uniqueId: UUID, name: String): ArcadePlayer {
        return if (loadedPlayerMap[uniqueId] != null) {
            loadedPlayerMap[uniqueId]!!
        } else {
            logger.info("Unable to find cached player data for $name [$uniqueId]. Fetching from MongoDb or Hypixel.")
            val playerData = loadPlayerDataFromMongoDb(uniqueId) ?: createPlayerDataFromHypixel(uniqueId, name)
            playerData.also {
                loadedPlayerMap[uniqueId] = it
            }
        }
    }

    private val refreshTime = 24.hours
    private suspend fun loadPlayerDataFromMongoDb(uniqueId: UUID) = playerDataCollection.findOneById(uniqueId)?.also {
        if ((Clock.System.now() - it.lastProfileUpdate) >= refreshTime) {
            val user = "${it.actualDisplayName} [${it.uuid}]"
            println("Updating user $user due to profile being too old.")
            val (value, duration) = measureTimedValue {
                fetchHypixelPlayerData(it.uuid, it.hypixelData?.displayName!!)
            }
            it.hypixelData = value
            it.updateHypixelData(false)
            it.lastProfileUpdate = Clock.System.now()
            savePlayerData(it)
            println("Updated user $user successfully (Took ${duration}).")
        }
    }

    val playerDataCollection by lazy {
        pluginInstance.database.getCollection<ArcadePlayer>("players")
    }

    suspend fun saveAndRemovePlayerData(uuid: UUID) {
        val id = ImpersonationManager.getImpersonation(uuid)?.uniqueId ?: uuid
        loadedPlayerMap[id]?.also {
            savePlayerData(it)
            loadedPlayerMap.remove(id)
        }
    }

    fun removePlayerData(uuid: UUID) {
        val id = ImpersonationManager.getImpersonation(uuid)?.uniqueId ?: uuid
        loadedPlayerMap[id]?.also {
            loadedPlayerMap.remove(id)
        }
    }

    suspend fun savePlayerData(it: ArcadeSender) {
        //Don't save Console Player Data
        if (it == consoleData) return

        logger.info("Saving player data for ${it.displayName} [${it.uuid}]")
        playerDataCollection.updateOneById(it.uuid, it, upsert())
    }

    suspend fun reloadProfile(player: ArcadePlayer) {
        reloadProfile(player, true) {}
    }

    fun storeInMemory(data: ArcadePlayer) {
        loadedPlayerMap[data.uuid] = data
    }
}

val consoleData = ArcadeConsole

suspend fun Player.getArcadeSender(): ArcadePlayer = (this as CommandSender).getArcadeSender() as ArcadePlayer

suspend fun CommandSender.getArcadeSender(): ArcadeSender {
    if (this !is Player) {
        return consoleData
    }
    val impersonation = ImpersonationManager.getImpersonation(uniqueId)
    if (impersonation != null)
        return PlayerDataManager.getPlayerData(impersonation.uniqueId, impersonation.name)

    return PlayerDataManager.getPlayerData(uniqueId, name)
}
