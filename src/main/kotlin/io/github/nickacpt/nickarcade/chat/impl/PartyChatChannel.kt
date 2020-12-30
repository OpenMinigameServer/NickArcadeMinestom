package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

object PartyChatChannel : AbstractChatChannel(ChatChannelType.PARTY) {
    override suspend fun checkSender(sender: PlayerData, origin: ChatMessageOrigin): Boolean {
        if (sender.currentParty == null) {
            if (origin == ChatMessageOrigin.CHAT && sender.currentChannel == ChatChannelType.PARTY) {
                sender.currentChannel = ChatChannelType.ALL
                sender.audience.sendMessage(separator {
                    append(text("You are not in a party and were moved to the ALL channel.", NamedTextColor.RED))
                })
            }
            return false
        }

        return true
    }

    override suspend fun getRecipients(sender: PlayerData, message: String): Audience {
        return sender.currentParty?.audience ?: Audience.empty()
    }
}