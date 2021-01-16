package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

object PartyChatChannel : AbstractChatChannel(ChatChannelType.PARTY) {
    override suspend fun checkSender(sender: ArcadeSender, origin: ChatMessageOrigin): Boolean {
        if (sender !is ArcadePlayer) return false

        if (sender.getCurrentParty() == null) {
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

    override suspend fun getRecipients(sender: ArcadeSender, message: String): Audience {
        return (sender as? ArcadePlayer)?.getCurrentParty()?.audience ?: Audience.empty()
    }
}