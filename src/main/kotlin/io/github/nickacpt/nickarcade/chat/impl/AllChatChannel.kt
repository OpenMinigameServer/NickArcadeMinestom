package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.utils.minestomAudiences
import net.kyori.adventure.audience.Audience
import kotlin.time.Duration
import kotlin.time.seconds

class AllChatChannel : AbstractChatChannel(ChatChannelType.ALL) {

    override suspend fun getPlayerRateLimit(sender: ArcadePlayer): Duration {
        return if (sender.hasAtLeastRank(HypixelPackageRank.VIP)) Duration.ZERO else 3.seconds
    }

    override suspend fun getRecipients(sender: ArcadeSender, message: String): Audience {
        return minestomAudiences.players()
    }
}