package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.chat.ChatEmote
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.cooldown
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.time.Duration

abstract class AbstractChatChannel(val type: ChatChannelType) {
    open val showActualValues: Boolean = type.useActualName

    open suspend fun checkSender(sender: PlayerData, origin: ChatMessageOrigin): Boolean = true

    abstract suspend fun getRecipients(sender: PlayerData, message: String): Audience

    open suspend fun getPlayerRateLimit(sender: PlayerData): Duration = Duration.ZERO

    open suspend fun formatMessage(
        sender: PlayerData,
        senderName: ComponentLike,
        message: ComponentLike
    ): ComponentLike {
        val chatColor =
            if (sender.hasAtLeastRank(HypixelPackageRank.VIP)) NamedTextColor.WHITE else NamedTextColor.GRAY

        return text {
            if (type.prefix != null)
                it.append(text("${type.name.toLowerCase().capitalize()}> ", type.prefix.color))
            it.append(senderName)
            it.append(text(": ", chatColor))
            it.append(text("", chatColor).append(message))
        }
    }

    suspend fun sendMessageInternal(sender: PlayerData, message: String, origin: ChatMessageOrigin) {
        var rateLimit = getPlayerRateLimit(sender)
        val player = sender.player

        if (!checkSender(sender, origin)) return

        if (player != null && rateLimit > Duration.ZERO && !player.cooldown("chat-$type", rateLimit)) {
            sender.audience.sendMessage(
                text {
                    it.append(
                        text(
                            "You can only chat once every ${rateLimit.inSeconds} seconds! Ranked users bypass this restriction!",
                            NamedTextColor.RED
                        )
                    )
                }
            )

            return
        }
        val recipients = getRecipients(sender, message)

        recipients.sendMessage(
            Identity.identity(sender.uuid),
            formatMessage(
                sender,
                text(sender.getChatName(showActualValues)),
                processChatMessage(sender, text(message))
            ).asComponent().hoverEvent(sender.computeHoverEventComponent(showActualValues))
        )
    }

    open fun processChatMessage(sender: PlayerData, message: Component): Component {
        var modifiedMessage = message
        if (sender.hasAtLeastRank(HypixelPackageRank.SUPERSTAR, showActualValues)) {
            modifiedMessage = processEmotes(modifiedMessage)
        }
        return modifiedMessage
    }

    private fun processEmotes(message: Component): Component {
        var modifiedMessage = message
        ChatEmote.values().forEach { emote ->
            modifiedMessage = modifiedMessage.replaceText {
                it.matchLiteral(emote.emote)
                it.replacement(emote.replacement)
            }
        }
        return modifiedMessage
    }
}