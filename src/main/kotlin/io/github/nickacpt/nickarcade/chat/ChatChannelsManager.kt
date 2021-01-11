package io.github.nickacpt.nickarcade.chat

import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.kotlin.extension.buildAndRegister
import io.github.nickacpt.nickarcade.chat.impl.*
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
        registerChannel(UserInputChannel)
    }

    private fun registerChannel(channel: AbstractChatChannel) {
        channels[channel.type] = channel
    }

    fun getChannelByType(id: ChatChannelType): AbstractChatChannel {
        return channels[id] ?: defaultChannel
    }

    fun registerChatChannelCommands(commandHelper: NickArcadeCommandHelper) {
        channels.filterNot { it.key.isInternal }.forEach { (type, channel) ->
            val smallLetter = type.name.first().toLowerCase()
            commandHelper.manager.buildAndRegister("${smallLetter}chat", aliases = arrayOf("${smallLetter}c")) {
                argument {
                    StringArgument.greedy("text")
                }
                handler {
                    command(it.sender, type.requiredRank) {
                        channel.sendMessageInternal(
                            it.sender.getPlayerData(),
                            it["text"],
                            ChatMessageOrigin.SHORTCUT_COMMAND
                        )
                    }
                }

            }
        }
    }
} 