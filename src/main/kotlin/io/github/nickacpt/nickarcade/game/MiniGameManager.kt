package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.data.player.PlayerData
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

    fun createGame(type: MiniGameType, definition: ArenaDefinition): Game {
        val miniGame = miniGames[type] ?: throw Exception("MiniGame ${type.friendlyName} was not registered.")

        return miniGame.createGame(definition)
    }

    private val playerGames = mutableMapOf<UUID, Game>()

    fun isInGame(player: PlayerData): Boolean = getCurrentGame(player) != null

    fun getCurrentGame(player: PlayerData): Game? {
        return playerGames[player.uuid]
    }

    fun addPlayer(game: Game, player: PlayerData) {
//        val playerParty = player.getCurrentParty(false)
//        if (playerParty != null) {
//            if (playerParty.isLeader(player)) {
//                playerParty.members.forEach {
//                    addPlayerInternal(it, game)
//                }
//            }
//            return
//        }

        addPlayerInternal(player, game)
    }

    private fun addPlayerInternal(
        player: PlayerData,
        game: Game
    ) {
        teleportPlayerToArena(player, game)
        playerGames[player.uuid] = game
        game.members.add(player)
        PlayerJoinGameEvent(game, player).callEvent()
    }

    private fun teleportPlayerToArena(
        player: PlayerData,
        game: Game
    ) {
        val minestomPlayer = player.player
        minestomPlayer?.setInstance(game.arena.instance, game.arenaDefinition.spawnPosition.toMinestom())
        minestomPlayer?.respawn()
    }

    fun removePlayer(game: Game, player: PlayerData) {
        game.members.removeIf { it == player }
        playerGames.remove(player.uuid)
        PlayerLeaveGameEvent(game, player).callEvent()
    }
}