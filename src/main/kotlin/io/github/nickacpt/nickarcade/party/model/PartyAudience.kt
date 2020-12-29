package io.github.nickacpt.nickarcade.party.model

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience

class PartyAudience(private val party: Party) : ForwardingAudience {
    override fun audiences(): Iterable<Audience> {
        val players = listOf(party.leader, *party.members.toTypedArray())
        return players.filter { it.isOnline }.map { it.audience }
    }
}