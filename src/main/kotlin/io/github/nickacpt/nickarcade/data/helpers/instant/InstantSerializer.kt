package io.github.nickacpt.nickarcade.data.helpers.instant

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.Instant

object InstantSerializer : StdSerializer<Instant>(Instant::class.java) {
    override fun serialize(value: Instant, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNumber(value.toEpochMilliseconds())
    }
}

