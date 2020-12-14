package io.github.nickacpt.nickarcade.chat

import io.github.nickacpt.nickarcade.data.PlayerData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.ComponentLike
import kotlin.time.Duration

abstract class AbstractChatChannel(val id: String) {
    abstract fun getRecipients(sender: PlayerData, message: String): Audience

    fun getPlayerRateLimit(sender: PlayerData): Duration = Duration.ZERO

    abstract fun formatMessage(sender: PlayerData, senderName: ComponentLike, message: ComponentLike): ComponentLike
}