package io.github.nickacpt.nickarcade.chat

import net.kyori.adventure.text.format.NamedTextColor

enum class ChatChannelType(val prefix: PrefixData?) {
    ALL(PrefixData(NamedTextColor.GREEN))
}