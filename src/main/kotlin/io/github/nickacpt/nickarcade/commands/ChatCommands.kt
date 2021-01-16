package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.utils.command
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED

object ChatCommands {
    @CommandMethod("chat <channel>")
    fun channelSwitchCommand(sender: ArcadeSender, @Argument("channel") channel: ChatChannelType) =
        command(sender, channel.requiredRank) {
            if (channel.isInternal) {
                sender.audience.sendMessage(
                    text(
                        "This is an internal chat channel and thus cannot be switched to.",
                        RED
                    )
                )
                return@command
            }
            sender.currentChannel = channel
            sender.audience.sendMessage(text("Changed current channel to $channel.", GREEN))
            PlayerDataManager.savePlayerData(sender)
        }
}