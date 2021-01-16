package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ExtraDataTag
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.minestom.server.entity.Player

val inputWaitingTag = ExtraDataTag.of<InputWaiting>("input_waiting")

data class InputWaiting(
    val oldChannel: ChatChannelType,
    val onSuccess: Player.(String) -> Unit,
    val isValid: Player.(String) -> Boolean
)

object ChatInput {

    suspend fun requestInput(
        player: Player,
        onSuccess: Player.(String) -> Unit,
        isValid: Player.(String) -> Boolean = { true }
    ) {
        val playerData = player.getArcadeSender()

        playerData[inputWaitingTag] = InputWaiting(playerData.currentChannel, onSuccess, isValid)
        playerData.currentChannel = ChatChannelType.USER_INPUT
    }

    fun performInput(sender: ArcadePlayer, content: String) {
        val input = sender[inputWaitingTag] ?: return
        val player = sender.player ?: return
        if (!input.isValid(player, content)) {
            sender.audience.sendMessage(
                Component.text(
                    "The input you gave '${content}' is not valid. Please enter a valid input.",
                    RED
                )
            )
            return
        }

        sender.currentChannel = input.oldChannel
        input.onSuccess(player, content)
    }

}