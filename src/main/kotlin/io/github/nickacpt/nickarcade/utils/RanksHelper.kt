package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import java.util.*
import kotlin.random.Random

object RanksHelper {
    private val ranksRange: EnumSet<HypixelPackageRank> =
        EnumSet.range(HypixelPackageRank.NORMAL, HypixelPackageRank.MVP_PLUS)

    fun randomPlayerOverrides() =
        PlayerOverrides(ranksRange.random(), networkLevel = Random.nextInt(1, 50).toLong(), isLegacyPlayer = false)
}