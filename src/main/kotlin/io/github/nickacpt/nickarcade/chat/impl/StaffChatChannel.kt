package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.data.getPlayerData
import io.github.nickacpt.nickarcade.utils.bukkitAudiences
import io.github.nickacpt.nickarcade.utils.filterSuspend
import net.kyori.adventure.audience.Audience

object StaffChatChannel : AbstractChatChannel(ChatChannelType.STAFF) {
    override suspend fun getRecipients(sender: PlayerData, message: String): Audience {
        return bukkitAudiences.filterSuspend {
            it.getPlayerData().hasAtLeastRank(HypixelPackageRank.HELPER)
        }
    }
}