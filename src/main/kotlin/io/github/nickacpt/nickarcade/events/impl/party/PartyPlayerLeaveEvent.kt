package io.github.nickacpt.nickarcade.events.impl.party

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.party.model.Party
import net.minestom.server.event.Event

class PartyPlayerLeaveEvent(val party: Party, val player: ArcadePlayer) : Event()