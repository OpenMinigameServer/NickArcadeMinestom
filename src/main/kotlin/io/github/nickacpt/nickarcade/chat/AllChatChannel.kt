package io.github.nickacpt.nickarcade.chat

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.utils.bukkitAudiences
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor

class AllChatChannel : AbstractChatChannel("ALL") {
    override fun getRecipients(sender: PlayerData, message: String): Audience {
        return bukkitAudiences.players()
    }

    override fun formatMessage(sender: PlayerData, senderName: ComponentLike, message: ComponentLike): ComponentLike {
        val chatColor =
            if (sender.hasAtLeastRank(HypixelPackageRank.VIP)) NamedTextColor.WHITE else NamedTextColor.GRAY

        return text {
            it.append(text("All> ", NamedTextColor.GREEN))
            it.append(senderName)
            it.append(text(": ", chatColor))
            it.append(message)
        }
    }
}