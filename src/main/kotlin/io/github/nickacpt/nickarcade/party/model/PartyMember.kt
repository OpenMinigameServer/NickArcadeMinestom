package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.player.PlayerData
import java.util.*

enum class MemberRole(
    val canModifySettings: Boolean = false,
    val canInvitePlayers: Boolean = false,
    val canReceiveMessages: Boolean = true
) {
    NONE(canReceiveMessages = false),
    PENDING_INVITE(canReceiveMessages = false),
    MEMBER,
    MODERATOR(false, true),
    LEADER(true, true)
}

data class PartyMember(val player: PlayerData, var role: MemberRole, val uuid: UUID = player.uuid) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is PartyMember) {
            return uuid == other.uuid
        } else if (other is PlayerData) {
            return uuid == other.uuid
        }

        return false
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}
