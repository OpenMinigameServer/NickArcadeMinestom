package io.github.nickacpt.hypixelapi.responses

import com.fasterxml.jackson.databind.JsonNode
import io.github.nickacpt.hypixelapi.models.HypixelPlayer

data class RawPlayerResult(val success: Boolean, val player: JsonNode?)

data class PlayerResult(val success: Boolean, val player: HypixelPlayer?)