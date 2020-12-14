package io.github.nickacpt.hypixelapi.utis.profile

import io.github.nickacpt.hypixelapi.utis.HypixelApi
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object ProfileApi {
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