package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank

data class PartySettings(
    val party: Party,
    @property:PartySetting("Private Game", HypixelPackageRank.SUPERSTAR, "private")
    var privateMode: Boolean = false,
    @property:PartySetting("Developer Game", HypixelPackageRank.ADMIN, aliases = ["developer", "dev"])
    var developerMode: Boolean = false,
)
