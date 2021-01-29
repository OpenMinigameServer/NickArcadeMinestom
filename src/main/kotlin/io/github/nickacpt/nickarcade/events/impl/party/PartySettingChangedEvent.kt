package io.github.nickacpt.nickarcade.events.impl.party

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.party.PartySetting
import io.github.nickacpt.nickarcade.party.model.Party
import net.minestom.server.event.Event
import kotlin.reflect.KProperty

class PartySettingChangedEvent<T>(
    val party: Party,
    val player: ArcadePlayer,
    val setting: PartySetting,
    val prop: KProperty<T>,
    val newValue: T
) : Event()