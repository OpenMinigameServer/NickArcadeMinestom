package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.minestomAudiences
import net.kyori.adventure.audience.Audience
import kotlin.time.Duration
import kotlin.time.seconds

class AllChatChannel : AbstractChatChannel(ChatChannelType.ALL) {

    override suspend fun getPlayerRateLimit(sender: PlayerData): Duration {
        return if (sender.hasAtLeastRank(HypixelPackageRank.VIP)) Duration.ZERO else 3.seconds
    }

    override suspend fun getRecipients(sender: PlayerData, message: String): Audience {
        return minestomAudiences.players()
    }
}