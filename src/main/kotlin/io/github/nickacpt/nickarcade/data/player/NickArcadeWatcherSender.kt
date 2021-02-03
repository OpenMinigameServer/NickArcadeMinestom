package io.github.nickacpt.nickarcade.data.player

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import net.kyori.adventure.audience.Audience
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import java.util.*

object NickArcadeWatcherSender : ArcadeSender(UUID.randomUUID()) {
    override val audience: Audience
        get() = Audience.empty()
    override val displayName: String
        get() = "NickArcade"
    override val commandSender: CommandSender
        get() = MinecraftServer.getCommandManager().consoleSender
    override var currentChannel: ChatChannelType
        get() = ChatChannelType.STAFF
        set(value) {}

    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean {
        return true
    }

    override fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String =
        "${MinecraftChatColor.RED}$displayName Watcher"
}