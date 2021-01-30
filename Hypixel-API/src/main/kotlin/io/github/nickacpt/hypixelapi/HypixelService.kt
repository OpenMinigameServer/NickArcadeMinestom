package io.github.nickacpt.hypixelapi

import io.github.nickacpt.hypixelapi.responses.KeyResult
import io.github.nickacpt.hypixelapi.responses.PlayerResult
import io.github.nickacpt.hypixelapi.responses.RawPlayerResult
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface HypixelService {

    @GET("/player")
    suspend fun getPlayerById(@Query("uuid") id: UUID): PlayerResult

    @GET("/player")
    suspend fun getPlayerByIdRaw(@Query("uuid") id: UUID): RawPlayerResult

    @GET("/player")
    @Deprecated("This endpoint was deprecated by Hypixel.")
    suspend fun getPlayerByName(@Query("name") name: String): PlayerResult

    @GET("/key")
    suspend fun getKeyInformation(): KeyResult
}