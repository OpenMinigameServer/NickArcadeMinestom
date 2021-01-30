package io.github.nickacpt.hypixelapi.responses

import io.github.nickacpt.hypixelapi.models.HypixelKeyRecord

data class KeyResult(val success: Boolean, val record: HypixelKeyRecord? = null)