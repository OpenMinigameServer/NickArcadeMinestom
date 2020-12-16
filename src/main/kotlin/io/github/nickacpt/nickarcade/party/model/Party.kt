package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.nickarcade.data.PlayerData
import java.util.*

data class Party(
    val leader: PlayerData,
    val members: List<PlayerData> = mutableListOf()
) {
    val id = UUID.randomUUID()
    var settings: PartySettings = PartySettings(this)
}