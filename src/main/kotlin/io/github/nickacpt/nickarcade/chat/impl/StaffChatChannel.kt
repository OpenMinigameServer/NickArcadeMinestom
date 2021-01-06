package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.filterSuspend
import io.github.nickacpt.nickarcade.utils.minestomAudiences
import net.kyori.adventure.audience.Audience

object StaffChatChannel : AbstractChatChannel(ChatChannelType.STAFF) {
    override val showActualValues: Boolean
        get() = true

    override suspend fun getRecipients(sender: PlayerData, message: String): Audience {
        return minestomAudiences.filterSuspend {
            it.getPlayerData().hasAtLeastRank(HypixelPackageRank.HELPER)
        }
    }
}