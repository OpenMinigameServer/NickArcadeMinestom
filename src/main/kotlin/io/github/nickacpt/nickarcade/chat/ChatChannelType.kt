package io.github.nickacpt.nickarcade.chat

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import net.kyori.adventure.text.format.NamedTextColor

enum class ChatChannelType(
    val prefix: PrefixData? = null,
    val requiredRank: HypixelPackageRank = HypixelPackageRank.NONE
) {
    ALL,
    PARTY(PrefixData(NamedTextColor.BLUE)),
    STAFF(PrefixData(NamedTextColor.AQUA), HypixelPackageRank.HELPER)
}