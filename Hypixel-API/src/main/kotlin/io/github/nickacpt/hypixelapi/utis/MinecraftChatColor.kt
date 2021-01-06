package io.github.nickacpt.hypixelapi.utis

enum class MinecraftChatColor(val chatFormat: String, val isSpecial: Boolean = false) {
    BLACK("§0"),
    DARK_BLUE("§1"),
    DARK_GREEN("§2"),
    DARK_AQUA("§3"),
    DARK_RED("§4"),
    DARK_PURPLE("§5"),
    GOLD("§6"),
    GRAY("§7"),
    DARK_GRAY("§8"),
    BLUE("§9"),
    GREEN("§a"),
    AQUA("§b"),
    RED("§c"),
    LIGHT_PURPLE("§d"),
    YELLOW("§e"),
    WHITE("§f"),

    OBFUSCATED("§k", true),
    BOLD("§l", true),
    STRIKETHROUGH("§m", true),
    UNDERLINED("§n", true),
    ITALIC("§o", true),
    RESET("§r", true)
    ;

    override fun toString(): String {
        return chatFormat
    }
}