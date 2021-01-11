package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.command
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.minestom.server.command.CommandSender

object ChatCommands {
    @CommandMethod("chat <channel>")
    fun channelSwitchCommand(sender: CommandSender, @Argument("channel") channel: ChatChannelType) =
        command(sender, channel.requiredRank) {
            val playerData = sender.getPlayerData()
            if (channel.isInternal) {
                playerData.audience.sendMessage(
                    text(
                        "This is an internal chat channel and thus cannot be switched to.",
                        RED
                    )
                )
                return@command
            }
            playerData.currentChannel = channel
            playerData.audience.sendMessage(text("Changed current channel to $channel.", GREEN))
            PlayerDataManager.savePlayerData(playerData)
        }
}