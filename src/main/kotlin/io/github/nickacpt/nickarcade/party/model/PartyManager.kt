package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.player.PlayerData
import java.util.*

object PartyManager {
    private val playerParty = mutableMapOf<UUID, Party>()

    fun createParty(player: PlayerData): Party {
        return Party().apply { addMember(player, role = MemberRole.LEADER) }
    }


    fun setPlayerParty(player: PlayerData, party: Party?) {
        if (party != null) {
            playerParty[player.uuid] = party
            return
        }

        playerParty.remove(player.uuid)
    }


    fun getParty(player: PlayerData): Party? {
        return playerParty[player.uuid]
    }
}