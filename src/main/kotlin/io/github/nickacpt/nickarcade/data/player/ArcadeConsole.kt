package io.github.nickacpt.nickarcade.data.player

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.utils.asAudience
import net.kyori.adventure.audience.Audience
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import java.util.*

object ArcadeConsole : ArcadeSender(UUID(0, 0)) {
    override val audience: Audience
        get() = commandSender.asAudience

    override val displayName: String
        get() = "Server Console"

    override val commandSender: CommandSender
        get() = MinecraftServer.getCommandManager().consoleSender

    override var currentChannel: ChatChannelType
        get() = ChatChannelType.ALL
        set(value) {}

    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean {
        return true
    }

    override fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String {
        return displayName
    }
}