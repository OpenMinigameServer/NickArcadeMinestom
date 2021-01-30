package io.github.nickacpt.hypixelapi.models

import java.util.*

data class HypixelKeyRecord(
    val key: UUID,
    val owner: UUID,
    val limit: Long,
    val queriesInPastMin: Long,
    val totalQueries: Long
)
