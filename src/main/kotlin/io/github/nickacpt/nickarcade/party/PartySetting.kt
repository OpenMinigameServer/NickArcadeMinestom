package io.github.nickacpt.nickarcade.party

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank

annotation class PartySetting(
    val description: String,
    val requiredRank: HypixelPackageRank = HypixelPackageRank.NONE,
    vararg val aliases: String
)
