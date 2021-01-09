package io.github.nickacpt.nickarcade.game

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience

class GameAudience(private val game: Game) : ForwardingAudience {
    override fun audiences(): Iterable<Audience> {
        return game.members.filter { it.isOnline }.map { it.audience }
    }
}