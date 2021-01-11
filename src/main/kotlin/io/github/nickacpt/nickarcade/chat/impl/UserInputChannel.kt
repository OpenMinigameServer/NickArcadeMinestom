package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.ChatInput
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

object UserInputChannel : AbstractChatChannel(ChatChannelType.USER_INPUT) {
    override suspend fun getRecipients(sender: PlayerData, message: String): Audience {
        return Audience.empty()
    }

    override fun processChatMessage(sender: PlayerData, message: Component): Component {
        ChatInput.performInput(sender, (message as TextComponent).content())
        return super.processChatMessage(sender, message)
    }
}