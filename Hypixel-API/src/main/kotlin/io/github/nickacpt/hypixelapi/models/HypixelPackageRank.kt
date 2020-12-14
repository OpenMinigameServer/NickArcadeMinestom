package io.github.nickacpt.hypixelapi.models

import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor

enum class HypixelPackageRank(val prefix: String, val chatMessagePrefix: String = "§r") {
    NONE("§7", "§7"),
    NORMAL(NONE),
    VIP("§a[VIP] "),
    VIP_PLUS("§a[VIP+] "),
    MVP("§b[MVP] "),
    MVP_PLUS("§b[MVP+] "),
    SUPERSTAR("§6[MVP++] "),
    YOUTUBER("§c[§rYOUTUBE§c] "),
    HELPER("§9[HELPER] "),
    MODERATOR("§4[MOD] "),
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
        return prefix
    }

    fun getPlusColor(player: HypixelPlayer): MinecraftChatColor {
        return if (this == VIP_PLUS) MinecraftChatColor.GOLD else player.rankPlusColor
    }

    constructor(otherRank: HypixelPackageRank) : this(otherRank.prefix, otherRank.chatMessagePrefix)
}