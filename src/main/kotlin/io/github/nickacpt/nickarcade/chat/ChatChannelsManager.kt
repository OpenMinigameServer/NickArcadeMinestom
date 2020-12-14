package io.github.nickacpt.nickarcade.chat

object ChatChannelsManager {
    private val channels = mutableMapOf<String, AbstractChatChannel>()
    private val defaultChannel = AllChatChannel()

    init {
        registerChannel(defaultChannel)
    }

    fun registerChannel(channel: AbstractChatChannel) {
        channels[channel.id] = channel
    }

    fun getChannelById(id: String): AbstractChatChannel {
        return channels[id] ?: defaultChannel
    }
}