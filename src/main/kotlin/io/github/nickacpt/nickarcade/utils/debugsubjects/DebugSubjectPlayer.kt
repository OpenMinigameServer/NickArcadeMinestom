package io.github.nickacpt.nickarcade.utils.debugsubjects

import io.github.nickacpt.nickarcade.data.player.PlayerData
import org.bukkit.entity.Player
import java.util.*

class DebugSubjectPlayer(val owner: Player, val target: PlayerData) : Player by owner {
    val prefix = "[${target.actualDisplayName}] "

    override fun getUniqueId(): UUID {
        return target.uuid
    }

    override fun getName(): String {
        return target.actualDisplayName
    }

    override fun sendMessage(sender: UUID?, messages: Array<out String>) {
        owner.sendMessage(sender, messages.map { prefix + it }.toTypedArray())
    }

    override fun sendMessage(sender: UUID?, message: String) {
        owner.sendMessage(sender, prefix + message)
    }

    override fun sendMessage(message: String) {
        owner.sendMessage(prefix + message)
    }

    override fun sendMessage(messages: Array<out String>) {
        owner.sendMessage(messages.map { prefix + it }.toTypedArray())
    }
}