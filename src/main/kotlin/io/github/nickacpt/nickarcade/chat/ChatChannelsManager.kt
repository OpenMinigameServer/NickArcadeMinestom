package io.github.nickacpt.nickarcade.chat

import io.github.nickacpt.nickarcade.chat.impl.AbstractChatChannel
import io.github.nickacpt.nickarcade.chat.impl.AllChatChannel

object ChatChannelsManager {
    internal val channels = mutableMapOf<ChatChannelType, AbstractChatChannel>()
    private val defaultChannel = AllChatChannel()

    init {
        registerChannel(defaultChannel)
    }

    private fun registerChannel(channel: AbstractChatChannel) {
        channels[channel.type] = channel
    }

    fun getChannelByType(id: ChatChannelType): AbstractChatChannel {
        return channels[id] ?: defaultChannel
    }
}