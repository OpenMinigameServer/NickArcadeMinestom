package io.github.nickacpt.nickarcade.chat

import cloud.commandframework.arguments.standard.StringArgument
import io.github.nickacpt.nickarcade.chat.impl.AbstractChatChannel
import io.github.nickacpt.nickarcade.chat.impl.AllChatChannel
import io.github.nickacpt.nickarcade.chat.impl.PartyChatChannel
import io.github.nickacpt.nickarcade.chat.impl.StaffChatChannel
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.NickArcadeCommandHelper

object ChatChannelsManager {
    private val channels = mutableMapOf<ChatChannelType, AbstractChatChannel>()
    private val defaultChannel = AllChatChannel()

    init {
        registerChannel(defaultChannel)
        registerChannel(StaffChatChannel)
        registerChannel(PartyChatChannel)
    }

    private fun registerChannel(channel: AbstractChatChannel) {
        channels[channel.type] = channel
    }

    fun getChannelByType(id: ChatChannelType): AbstractChatChannel {
        return channels[id] ?: defaultChannel
    }

    fun registerChatChannelCommands(commandHelper: NickArcadeCommandHelper) {
        channels.forEach { (type, channel) ->
            val smallLetter = type.name.first().toLowerCase()
            commandHelper.manager.command(
                commandHelper.manager.commandBuilder("${smallLetter}chat", "${smallLetter}c")
                    .argument(String::class.java, "text") {
                        it.asRequired()
                            .withParser(StringArgument.StringParser(
                                StringArgument.StringMode.GREEDY
                            ) { _, _ -> mutableListOf() })
                    }.handler {
                        command(it.sender, type.requiredRank) {
                            channel.sendMessageInternal(
                                it.sender.getPlayerData(),
                                it["text"],
                                ChatMessageOrigin.SHORTCUT_COMMAND
                            )
                        }
                    }
                    .build()
            )
        }
    }
} 