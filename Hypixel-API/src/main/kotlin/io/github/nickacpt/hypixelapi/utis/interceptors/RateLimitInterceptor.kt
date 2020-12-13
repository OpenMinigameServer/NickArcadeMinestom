package io.github.nickacpt.hypixelapi.utis.interceptors

import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response

@Suppress("UnstableApiUsage")
class RateLimitInterceptor(callsPerMinute: Int) : Interceptor {
    private val limiter = RateLimiter.create(callsPerMinute / 60.0)

    override fun intercept(chain: Interceptor.Chain): Response {
        limiter.acquire(1)
        return chain.proceed(chain.request())
    }
}
