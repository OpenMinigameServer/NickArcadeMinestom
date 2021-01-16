package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.utils.ChatInput
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

object UserInputChannel : AbstractChatChannel(ChatChannelType.USER_INPUT) {
    override suspend fun getRecipients(sender: ArcadeSender, message: String): Audience {
        return Audience.empty()
    }

    override fun processChatMessage(sender: ArcadeSender, message: Component): Component {
        if (sender !is ArcadePlayer) return super.processChatMessage(sender, message)
        ChatInput.performInput(sender, (message as TextComponent).content())
        return super.processChatMessage(sender, message)
    }
}