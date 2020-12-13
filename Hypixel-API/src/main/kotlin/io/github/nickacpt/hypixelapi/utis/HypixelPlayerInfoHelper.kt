package io.github.nickacpt.hypixelapi.utis

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import io.github.nickacpt.hypixelapi.HypixelService
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import java.util.*
import java.util.concurrent.TimeUnit

class HypixelPlayerInfoHelper(private val hypixelService: HypixelService) {
    private val playerCache: LoadingCache<UUID, HypixelPlayer?> = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES).build(CacheLoader.from { _ -> null })
    private val objectMapper by lazy { HypixelApi.objectMapper }

    suspend fun tryGetPlayerById(id: UUID): HypixelPlayer? {
        return playerCache.getIfPresent(id) ?: hypixelService.getPlayerByIdRaw(id).player?.let {
            createHypixelPlayerWithRaw(it)
        }?.also { playerCache.put(id, it) }
    }

    private fun createHypixelPlayerWithRaw(it: JsonNode): HypixelPlayer? {
        val result = objectMapper.treeToValue<HypixelPlayer>(it)
        result?.rawJsonNode = it
        return result
    }

    fun hasPlayerById(uniqueId: UUID): Boolean = playerCache.getIfPresent(uniqueId) != null
}