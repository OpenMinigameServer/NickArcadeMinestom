package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.utils.bukkitAudiences
import net.kyori.adventure.audience.Audience
import kotlin.time.Duration
import kotlin.time.seconds

class AllChatChannel : AbstractChatChannel(ChatChannelType.ALL) {

    override fun getPlayerRateLimit(sender: PlayerData): Duration {
        return if (sender.hasAtLeastRank(HypixelPackageRank.VIP)) Duration.ZERO else 3.seconds
    }

    override fun getRecipients(sender: PlayerData, message: String): Audience {
        return bukkitAudiences.players()
    }
}