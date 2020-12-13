package io.github.nickacpt.hypixelapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import io.github.nickacpt.hypixelapi.utis.NetworkLeveling

data class HypixelPlayer(
    val playerName: String,
    val displayName: String,
    val newPackageRank: HypixelPackageRank? = null,
    val monthlyPackageRank: HypixelPackageRank? = null,
    val networkExp: Double,
    @JsonProperty("rank") val legacyRank: HypixelPackageRank? = null,
    @JsonProperty("prefix") val userPrefix: String? = null,
    @Transient var rawJsonNode: JsonNode? = null
) {
    @get:JsonIgnore
    val networkLevel: Long
        get() = NetworkLeveling.getLevel(networkExp).toLong()

    @get:JsonIgnore
    val effectiveRank: HypixelPackageRank
        get() = legacyRank ?: monthlyPackageRank.takeUnless { it?.equals(HypixelPackageRank.NONE) ?: false }
        ?: newPackageRank
        ?: HypixelPackageRank.NORMAL

    @get:JsonIgnore
    val effectivePrefix: String
        get() = userPrefix ?: (effectiveRank.prefix)

}
