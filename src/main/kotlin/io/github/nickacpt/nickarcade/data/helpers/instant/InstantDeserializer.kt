package io.github.nickacpt.nickarcade.data.helpers.instant

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlinx.datetime.Instant

object InstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        return Instant.fromEpochMilliseconds(p.longValue)
    }
}