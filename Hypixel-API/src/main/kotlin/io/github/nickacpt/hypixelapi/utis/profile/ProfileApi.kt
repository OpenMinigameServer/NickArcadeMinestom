package io.github.nickacpt.hypixelapi.utis.profile

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ProfileApi {

    private val profileCache: LoadingCache<UUID, Profile?> = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES).build(CacheLoader.from { _ -> null })
    private val profileNameCache: LoadingCache<String, Profile?> = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES).build(CacheLoader.from { _ -> null })

    suspend fun getProfileById(id: UUID): Profile? {
        return profileCache.getIfPresent(id) ?: getProfileService().findById(id)?.takeIf { !it.isError }?.also {
            profileCache.put(id, it)
            profileNameCache.put(it.name!!, it)
        }
    }

    suspend fun getProfileByName(name: String): Profile? {
        return profileNameCache.getIfPresent(name) ?: getProfileService().findById(name)?.takeIf { !it.isError }?.also {
            profileNameCache.put(it.name!!, it)
            profileCache.put(it.uuid!!, it)
        }
    }

    fun getProfileService(): ProfileService {
        return Retrofit.Builder().baseUrl("https://api.ashcon.app/mojang/v2/")
            .addConverterFactory(
                JacksonConverterFactory.create(
                    HypixelApi.objectMapper
                )
            )
            .build()
            .create(ProfileService::class.java)

    }
}