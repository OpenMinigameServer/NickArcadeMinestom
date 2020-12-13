package io.github.nickacpt.hypixelapi.utis

import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.*

object UUIDConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return if (type != UUID::class.java) {
            super.stringConverter(type, annotations, retrofit)
        } else Converter<UUID, String> { it.toString().replace("-", "") }
    }
}