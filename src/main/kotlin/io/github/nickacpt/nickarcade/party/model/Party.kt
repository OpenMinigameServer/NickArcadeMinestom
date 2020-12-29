package io.github.nickacpt.nickarcade.party.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.shynixn.mccoroutine.launchAsync
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.utils.pluginInstance
import io.github.nickacpt.nickarcade.utils.separator
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import kotlin.time.minutes

val partyExpiryTime = 1.minutes

data class Party(
    val leader: PlayerData,
    val members: MutableList<PlayerData> = mutableListOf(),
    val pendingInvites: MutableList<PartyPendingInvite> = mutableListOf()
) {
    fun hasPendingInvite(player: PlayerData): Boolean {
        return pendingInvites.any { it.player == player }
    }

    fun hasPendingInvite(invite: PartyPendingInvite): Boolean {
        return pendingInvites.contains(invite)
    }

    fun invitePlayer(sender: PlayerData, target: PlayerData) {
        if (hasPendingInvite(target)) {
            sender.audience.sendMessage(separator {
                append(text(target.getChatName(true)))
                append(text(" has already been invited to the party.", NamedTextColor.RED))
            })
            return
        }
        if (!target.isOnline) {
            sender.audience.sendMessage(separator {
                append(text("You cannot invite that player since they're not online.", NamedTextColor.RED))
            })
            return
        }

        pluginInstance.launchAsync {
            val invite = PartyPendingInvite(target)
            pendingInvites.add(invite)
            delay(partyExpiryTime)
            if (hasPendingInvite(invite)) {
                pendingInvites.remove(invite)
            }
        }
        audience.sendMessage(separator(NamedTextColor.BLUE) {
            append(text(sender.getChatName(true)))
            append(text(" invited ", NamedTextColor.YELLOW))
            append(text(target.getChatName(true)))
            append(text(" to the party! They have ", NamedTextColor.YELLOW))
            append(text(partyExpiryTime.inSeconds.toInt(), NamedTextColor.RED))
            append(text(" seconds to accept.", NamedTextColor.YELLOW))
        })
    }

    val id: UUID = UUID.randomUUID()
    var settings: PartySettings = PartySettings(this)

    @JsonIgnore
    val audience = PartyAudience(this)
}