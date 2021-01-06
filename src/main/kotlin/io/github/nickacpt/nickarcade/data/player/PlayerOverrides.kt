package io.github.nickacpt.nickarcade.data.player

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor

data class PlayerOverrides(
    var rankOverride: HypixelPackageRank? = null,
    var prefixOverride: String? = null,
    var monthlyRankColorOverride: MinecraftChatColor? = null,
    var rankPlusColorOverride: MinecraftChatColor? = null,
    val networkLevel: Long? = null
)