package io.github.nickacpt.nickarcade.party.model

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience

class PartyAudience(private val party: Party) : ForwardingAudience {
    override fun audiences(): Iterable<Audience> {
        return party.membersList.filter { it.role.canReceiveMessages }.map { it.player }.filter { it.isOnline }
            .map { it.audience }
    }
}