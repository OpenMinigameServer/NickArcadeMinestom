package io.github.nickacpt.hypixelapi.models

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

    constructor(otherRank: HypixelPackageRank) : this(otherRank.prefix, otherRank.chatMessagePrefix)
}