package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.events.impl.game.PlayerJoinGameEvent
import io.github.nickacpt.nickarcade.events.impl.game.PlayerLeaveGameEvent
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import io.github.nickacpt.nickarcade.game.definition.MiniGameType
import io.github.nickacpt.nickarcade.game.impl.BedWarsMiniGame
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import java.util.*

object MiniGameManager {
    private val miniGames = mutableMapOf<MiniGameType, BaseMiniGame>()

    init {
        registerMiniGame(BedWarsMiniGame)
    }

    private fun registerMiniGame(miniGame: BaseMiniGame) {
        miniGames[miniGame.type] = miniGame
    }

    fun createGame(type: MiniGameType, definition: ArenaDefinition): Game? {
        val miniGame = miniGames[type] ?: throw Exception("MiniGame ${type.friendlyName} was not registered.")

        return miniGame.createGame(definition)
    }

    private val playerGames = mutableMapOf<UUID, Game>()

    fun isInGame(player: ArcadePlayer): Boolean = getCurrentGame(player) != null

    fun getCurrentGame(player: ArcadePlayer): Game? {
        return playerGames[player.uuid]
    }

    suspend fun addPlayer(game: Game, player: ArcadePlayer) {
        val playerParty = player.getCurrentParty(false)
        if (playerParty != null) {
            if (playerParty.isLeader(player)) {
                playerParty.membersList.forEach {
                    addPlayerInternal(it.player, game)
                }
            }
            return
        }

        addPlayerInternal(player, game)
    }

    suspend fun addPlayerInternal(
        player: ArcadePlayer,
        game: Game
    ) {
        playerGames[player.uuid] = game
        PlayerDataManager.reloadProfile(player)

        teleportPlayerToArena(player, game)

        game.members.add(player)
        PlayerJoinGameEvent(game, player, game.playerCount).callEvent()
    }

    private fun teleportPlayerToArena(
        player: ArcadePlayer,
        game: Game
    ) {
        val minestomPlayer = player.player
        minestomPlayer?.setInstance(game.arena, game.arenaDefinition.spawnPosition.toMinestom())
        minestomPlayer?.respawn()
    }

    fun removePlayer(game: Game, player: ArcadePlayer) {
        game.members.removeIf { it == player }
        playerGames.remove(player.uuid)
        PlayerLeaveGameEvent(game, player).callEvent()
    }
}