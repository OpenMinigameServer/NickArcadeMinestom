package io.github.nickacpt.nickarcade.party.model

import java.util.*

object PartyHelper {
    private val partyCache = mutableMapOf<UUID, Party>()

    fun storeParty(id: UUID, party: Party) {
        partyCache[id] = party
    }

    fun removeParty(id: UUID) {
        partyCache.remove(id)
    }

    fun getCachedParty(id: UUID): Party? {
        return partyCache[id]
    }
}