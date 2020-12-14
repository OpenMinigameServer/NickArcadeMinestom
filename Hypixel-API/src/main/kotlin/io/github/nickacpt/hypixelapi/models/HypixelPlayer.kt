package io.github.nickacpt.hypixelapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.hypixelapi.utis.NetworkLeveling

data class HypixelPlayer(
    val playerName: String? = "",
    @JsonProperty("displayname") val displayName: String? = "",
    val newPackageRank: HypixelPackageRank? = null,
    val monthlyPackageRank: HypixelPackageRank? = null,
    val networkExp: Double? = 0.0,
    @JsonProperty("rank") val legacyRank: HypixelPackageRank? = null,
    @JsonProperty("prefix") val userPrefix: String? = null,
    @Transient var rawJsonNode: JsonNode? = null,
    var rankPlusColor: MinecraftChatColor = MinecraftChatColor.RED,
    var monthlyRankColor: MinecraftChatColor = MinecraftChatColor.GOLD,
) {
    @get:JsonIgnore
    val networkLevel: Long
        get() = NetworkLeveling.getLevel(networkExp ?: 0.0).toLong()

    @get:JsonIgnore
    val effectiveRank: HypixelPackageRank
        get() = legacyRank ?: monthlyPackageRank.takeUnless { it?.equals(HypixelPackageRank.NONE) ?: false }
        ?: newPackageRank
        ?: HypixelPackageRank.NORMAL

    @get:JsonIgnore
    val effectivePrefix: String
        get() = fixUserPrefixSpacing() ?: (effectiveRank.computePrefixForPlayer(
            effectiveRank.getPlusColor(this),
            monthlyRankColor
        ))

    private fun fixUserPrefixSpacing(): String? {
        return userPrefix?.let { if (!it.endsWith(' ')) "$it " else it }
    }

}
