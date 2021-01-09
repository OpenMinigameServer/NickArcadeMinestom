package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*

object PartyManager {
    private val playerParty = mutableMapOf<UUID, Party>()

    fun createParty(leader: PlayerData): Party {
        val party = Party(leader)
        playerParty[leader.uuid] = party
        addMember(party, leader)
        return party
    }

    fun addMember(party: Party, player: PlayerData) {
        if (party.members.none { it == player })
            party.members.add(player)
        playerParty[player.uuid] = party
    }

    fun removeMember(party: Party, player: PlayerData) {
        if (party.totalMembersCount > 1 && party.isLeader(player)) {
            val oldOwner = party.leader
            val newOwner = party.nonLeaderMembers.first()
            party.switchOwner(newOwner)
            party.audience.sendMessage(separator {
                append(Component.text(newOwner.getChatName(true)))
                append(Component.text(" is now the party leader because ", NamedTextColor.YELLOW))
                append(Component.text(oldOwner.getChatName(true)))
                append(Component.text(" left the party.", NamedTextColor.YELLOW))
            })
        }
        party.members.remove(player)
        playerParty.remove(player.uuid)
    }

    fun getParty(player: PlayerData): Party? {
        return playerParty[player.uuid]
    }
}