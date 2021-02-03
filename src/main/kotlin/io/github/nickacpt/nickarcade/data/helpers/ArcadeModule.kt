package io.github.nickacpt.nickarcade.data.helpers

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.addDeserializer
import com.fasterxml.jackson.module.kotlin.addSerializer
import io.github.nickacpt.nickarcade.data.helpers.instant.InstantDeserializer
import io.github.nickacpt.nickarcade.data.helpers.instant.InstantSerializer
import kotlinx.datetime.Instant

object ArcadeModule : SimpleModule("NickArcade") {
    init {
        addSerializer(Instant::class, InstantSerializer)
        addDeserializer(Instant::class, InstantDeserializer)
    }
}