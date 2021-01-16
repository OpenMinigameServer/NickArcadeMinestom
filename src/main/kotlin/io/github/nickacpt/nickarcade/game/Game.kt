package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import net.kyori.adventure.audience.Audience
import net.minestom.server.instance.Instance
import java.util.*

data class Game(
    val id: UUID,
    val miniGame: BaseMiniGame,
    val arenaDefinition: ArenaDefinition,
    val arena: Instance
) {
    suspend fun addPlayer(player: ArcadePlayer) {
        MiniGameManager.addPlayer(this, player)
    }

    val members: MutableList<ArcadePlayer> = mutableListOf()
    val state: GameState = GameState.WAITING_FOR_PLAYERS
    val audience: Audience = GameAudience(this)

    val playerCount: Int
        get() = members.count()

    val maxPlayerCount: Int = arenaDefinition.maxPlayers

}
