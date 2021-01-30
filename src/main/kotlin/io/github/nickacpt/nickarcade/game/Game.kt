package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import io.github.nickacpt.nickarcade.party.model.Party
import io.github.nickacpt.nickarcade.utils.timers.TimerBase
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.instance.Instance
import java.util.*

abstract class Game(
    val id: UUID,
    private val miniGame: BaseMiniGame,
    val arenaDefinition: ArenaDefinition,
    val arena: Instance
) {
    fun debug(message: String) {
        audience.sendActionBar(Component.text("[DEBUG] $message", NamedTextColor.GRAY))
    }

    private val timers = mutableMapOf<GameState, MutableList<TimerBase>>().apply {
        GameState.values().forEach { this[it] = mutableListOf() }
    }

    private val currentTimers: List<TimerBase>
        get() = timers[state]!!

    private fun addTimer(state: GameState, timer: TimerBase) {
        timers[state]!!.add(timer)
    }

    var hostParty: Party? = null
        set(value) {
            MiniGameManager.notifyPartyHost(this, value)
            field = value
        }

    fun refreshTimers(newState: GameState = state) {
        if (state == newState) return

        //Stop all current timers
        currentTimers.forEach { it.stop() }

        //Start timers that are supposed to be started
        timers[newState]!!.forEach { it.restart() }
    }

    fun isPrivateGame() = hostParty?.isPrivateGameParty() == true

    fun isDeveloperGame() = hostParty?.isDeveloperGameParty() == true

    suspend fun addPlayer(player: ArcadePlayer) {
        MiniGameManager.addPlayer(this, player)
    }

    init {
        addTimer(GameState.WAITING_FOR_PLAYERS, LobbyWaitTimerBase(this, miniGame.lobbyWaitTime))
    }

    val members: MutableList<ArcadePlayer> = mutableListOf()

    var state: GameState = GameState.NONE
        set(value) {
            refreshTimers(value)
            field = value
            debug("Set game state to $state")
        }

    val audience: Audience = GameAudience(this)

    val playerCount: Int
        get() = members.count()

    val maxPlayerCount: Int = arenaDefinition.maxPlayers

}
