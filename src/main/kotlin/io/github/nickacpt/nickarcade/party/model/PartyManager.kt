package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import java.util.*

object PartyManager {
    private val playerParty = mutableMapOf<UUID, Party>()

    fun createParty(player: ArcadePlayer): Party {
        return Party().apply { addMember(player, role = MemberRole.LEADER) }
    }


    fun setPlayerParty(player: ArcadePlayer, party: Party?) {
        if (party != null) {
            playerParty[player.uuid] = party
            return
        }

        playerParty.remove(player.uuid)
    }


    fun getParty(player: ArcadePlayer): Party? {
        return playerParty[player.uuid]
    }
}