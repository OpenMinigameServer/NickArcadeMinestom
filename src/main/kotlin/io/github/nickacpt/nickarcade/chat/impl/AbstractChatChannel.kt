package io.github.nickacpt.nickarcade.chat.impl

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.chat.ChatEmote
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
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

    open suspend fun checkSender(sender: ArcadeSender, origin: ChatMessageOrigin): Boolean = true

    abstract suspend fun getRecipients(sender: ArcadeSender, message: String): Audience

    open suspend fun getSenderRateLimit(sender: ArcadeSender): Duration {
        return if (sender is ArcadePlayer) {
            getPlayerRateLimit(sender)
        } else
            Duration.ZERO
    }

    open suspend fun getPlayerRateLimit(sender: ArcadePlayer): Duration = Duration.ZERO

    open suspend fun formatMessage(
        sender: ArcadeSender,
        senderName: ComponentLike,
        message: ComponentLike
    ): ComponentLike {
        val chatColor =
            if (sender.hasAtLeastRank(
                    HypixelPackageRank.VIP,
                    showActualValues
                )
            ) NamedTextColor.WHITE else NamedTextColor.GRAY

        return text {
            if (type.prefix != null)
                it.append(text("${type.name.toLowerCase().capitalize()}> ", type.prefix.color))
            it.append(senderName)
            it.append(text(": ", chatColor))
            it.append(text("", chatColor).append(message))
        }
    }

    suspend fun sendMessageInternal(sender: ArcadeSender, message: String, origin: ChatMessageOrigin) {
        val rateLimit = getSenderRateLimit(sender)

        if (!checkSender(sender, origin)) return

        val isInCooldown = sender is ArcadePlayer && sender.player?.cooldown("chat-$type", rateLimit) == true
        if (rateLimit > Duration.ZERO && !isInCooldown) {
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

        //TODO: Messages leak nicked Players
        val hoverComponent = (sender as? ArcadePlayer)?.computeHoverEventComponent(showActualValues)
        recipients.sendMessage(
            Identity.identity(sender.uuid),
            formatMessage(
                sender,
                text(sender.getChatName(showActualValues, false)),
                processChatMessage(sender, text(message))
            ).asComponent().hoverEvent(hoverComponent)
        )
    }

    open fun processChatMessage(sender: ArcadeSender, message: Component): Component {
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