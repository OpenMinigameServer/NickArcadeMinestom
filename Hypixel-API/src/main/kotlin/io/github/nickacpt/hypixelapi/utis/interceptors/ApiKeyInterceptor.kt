package io.github.nickacpt.hypixelapi.utis.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class ApiKeyInterceptor(val apiKey: UUID) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url()

        // Request customization: add request headers
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("key", apiKey.toString())
            .build()

        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}