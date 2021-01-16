package io.github.nickacpt.nickarcade.party.model

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.events.impl.party.PartySettingChangedEvent
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation

data class PartySettings(
    val party: Party,
    @property:PartySetting("Private Game", HypixelPackageRank.SUPERSTAR, "private")
    var privateMode: Boolean = false,
    @property:PartySetting("Developer Game", HypixelPackageRank.ADMIN, aliases = ["developer", "dev"])
    var developerMode: Boolean = false,
    @property:PartySetting("All Nicked", HypixelPackageRank.NONE, "allnicked", "allnick")
    var allNick: Boolean = false,
) {
    fun <T> setPropertyAndNotify(sender: ArcadePlayer, prop: KMutableProperty1<PartySettings, T>, value: T) {
        prop.set(this, value)
        prop.findAnnotation<PartySetting>()?.let { setting ->
            PartySettingChangedEvent(
                party,
                sender,
                setting,
                prop,
                value
            ).callEvent()
        }
    }
}