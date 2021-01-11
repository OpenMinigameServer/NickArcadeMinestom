package io.github.nickacpt.nickarcade.display

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.utils.interop.PlayerProfile

data class NickContext(
    var acceptedTerms: Boolean? = null,
    var rank: HypixelPackageRank? = null,
    var skin: PlayerProfile? = null
)