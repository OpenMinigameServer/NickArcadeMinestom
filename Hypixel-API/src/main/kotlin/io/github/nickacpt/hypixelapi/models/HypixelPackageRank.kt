package io.github.nickacpt.hypixelapi.models

import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor

enum class HypixelPackageRank(val defaultPrefix: String, private val chatMessagePrefix: String = "§r") {
    NONE("§7", "§7"),
    NORMAL(NONE),
    VIP("§a[VIP] "),
    VIP_PLUS("§a[VIP§6+§a] "),
    MVP("§b[MVP] "),
    MVP_PLUS("§b[MVP§c+§b] "),
    SUPERSTAR("§6[MVP§c++§6] "),
    YOUTUBER("§c[§fYOUTUBE§c] "),
    HELPER("§9[HELPER] "),
    MODERATOR("§2[MOD] "),
    ADMIN("§c[ADMIN] ");

    fun computePrefixForPlayer(player: HypixelPlayer): String {
        return computePrefixForPlayer(player.rankPlusColor, player.monthlyRankColor)
    }

    fun computePrefixForPlayer(
        rankPlusColor: MinecraftChatColor, monthlyRankColor: MinecraftChatColor
    ): String {
        val plusColorFormat = rankPlusColor.chatFormat
        if (this == SUPERSTAR) {
            val superstarColor = monthlyRankColor.chatFormat
            return "$superstarColor[MVP$plusColorFormat++$superstarColor] "
        } else if (this == MVP_PLUS) {
            return "§b[MVP$plusColorFormat+§b] "
        }
        return defaultPrefix
    }

    fun getPlusColor(player: HypixelPlayer): MinecraftChatColor {
        return if (this == VIP_PLUS) MinecraftChatColor.GOLD else player.rankPlusColor
    }

    constructor(otherRank: HypixelPackageRank) : this(otherRank.defaultPrefix, otherRank.chatMessagePrefix)
}