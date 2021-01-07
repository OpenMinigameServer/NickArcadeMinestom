package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.player.PlayerData
import java.util.*

object PartyManager {
    private val playerParty = mutableMapOf<UUID, Party>()

    fun createParty(leader: PlayerData): Party {
        val party = Party(leader)
        playerParty[leader.uuid] = party
        return party
    }

    fun addMember(party: Party, player: PlayerData) {
        if (party.members.none { it == player })
            party.members.add(player)
        playerParty[player.uuid] = party
    }

    fun removeMember(party: Party, player: PlayerData) {
        party.members.remove(player)
        playerParty.remove(player.uuid)
    }

    fun getParty(player: PlayerData): Party? {
        return playerParty[player.uuid]
    }
}