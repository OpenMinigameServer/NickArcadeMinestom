package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.utils.filterSuspend
import io.github.nickacpt.nickarcade.utils.minestomAudiences
import net.kyori.adventure.audience.Audience

object StaffChatChannel : AbstractChatChannel(ChatChannelType.STAFF) {
    override val showActualValues: Boolean
        get() = true

    override suspend fun getRecipients(sender: ArcadeSender, message: String): Audience {
        return minestomAudiences.filterSuspend {
            it.getArcadeSender().hasAtLeastRank(HypixelPackageRank.HELPER, true)
        }
    }
}