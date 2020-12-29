package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.PlayerData

data class PartyPendingInvite(val player: PlayerData) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PartyPendingInvite

        if (player != other.player) return false

        return true
    }

    override fun hashCode(): Int {
        return player.hashCode()
    }
}