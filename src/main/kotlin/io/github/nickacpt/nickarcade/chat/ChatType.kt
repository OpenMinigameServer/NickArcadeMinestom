package io.github.nickacpt.nickarcade.chat

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank

enum class ChatType(val requiredRank: HypixelPackageRank) {
    ALL(HypixelPackageRank.NONE),
    PARTY(HypixelPackageRank.NONE),
    STAFF(HypixelPackageRank.MODERATOR)
}