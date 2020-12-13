package io.github.nickacpt.hypixelapi.utis

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.nickacpt.hypixelapi.HypixelService
import io.github.nickacpt.hypixelapi.utis.interceptors.ApiKeyInterceptor
import io.github.nickacpt.hypixelapi.utis.interceptors.RateLimitInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.*

object HypixelApi {

    private fun getHttpClient(apiKey: UUID): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .addInterceptor(RateLimitInterceptor(120))
            .build()
    }

    fun getService(apiKey: UUID): HypixelService {
        return Retrofit.Builder().baseUrl("https://api.hypixel.net")
            .client(getHttpClient(apiKey))
            .addConverterFactory(
                JacksonConverterFactory.create(
                    objectMapper
                )
            )
            .addConverterFactory(UUIDConverterFactory)
            .build()
            .create(HypixelService::class.java)
    }

    val objectMapper by lazy { configureMapper(jacksonObjectMapper()) }


    fun configureMapper(mapper: ObjectMapper): ObjectMapper {
        return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
    }
}