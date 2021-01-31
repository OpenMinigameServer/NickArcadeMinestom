package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.BaseMiniGame
import io.github.nickacpt.nickarcade.game.definition.MiniGameMode
import io.github.nickacpt.nickarcade.party.model.Party
import io.github.nickacpt.nickarcade.utils.ScoreboardManager
import io.github.nickacpt.nickarcade.utils.timers.TimerBase
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.minestom.server.instance.Instance
import java.util.*

abstract class Game(
    val id: UUID,
    val miniGame: BaseMiniGame,
    val mode: MiniGameMode,
    val arenaDefinition: ArenaDefinition,
    val arena: Instance
) {
    fun debug(message: String) {
        audience.sendActionBar(text("[DEBUG] $message", NamedTextColor.GRAY))
    }

    private val timers = mutableMapOf<GameState, MutableList<TimerBase>>().apply {
        GameState.values().forEach { this[it] = mutableListOf() }
    }

    private val currentTimers: List<TimerBase>
        get() = timers[state]!!

    var hostParty: Party? = null
        set(value) {
            MiniGameManager.notifyPartyHost(this, value)
            field = value
        }

    fun refreshTimers(newState: GameState = state) {
        if (state == newState) return

        //Stop all current timers
        stopTimers()

        //Start timers that are supposed to be started
        timers[newState]!!.forEach { it.restart() }
    }

    fun stopTimers() {
        currentTimers.forEach { it.stop() }
    }

    fun isPrivateGame() = hostParty?.isPrivateGameParty() == true

    fun isDeveloperGame() = hostParty?.isDeveloperGameParty() == true

    private fun addTimer(state: GameState, timer: TimerBase) {
        timers[state]!!.add(timer)
    }

    suspend fun addPlayer(player: ArcadePlayer) {
        MiniGameManager.addPlayer(this, player)
    }

    suspend fun refreshMemberScoreboards() {
        members.forEach { it.player?.let { p -> ScoreboardManager.refreshScoreboard(p) } }
    }

    val lobbyWaitTimer by lazy { LobbyWaitTimerBase(this, miniGame.lobbyWaitTime) }

    init {
        addTimer(GameState.WAITING_FOR_PLAYERS, lobbyWaitTimer)
    }

    open fun computeScoreboard(player: ArcadePlayer): List<Component> {
        if (state == GameState.WAITING_FOR_PLAYERS) {
            return mutableListOf<Component>().apply {
                add(text("Map: ").append(text(arenaDefinition.name, GREEN)))
                add(text("Players: ").append(text("${playerCount}/${arenaDefinition.maxPlayers}", GREEN)))

                add(empty())

                if (lobbyWaitTimer.isWaiting) {
                    add(text("Waiting..."))
                } else {
                    add(
                        text("Starting in ").append(
                            text(
                                "${lobbyWaitTimer.remainingDuration.inSeconds.toInt()}s",
                                GREEN
                            )
                        )
                    )
                }

                add(empty())
                add(text("Mode: ").append(text(mode.friendlyName, GREEN)))
            }
        }

        return emptyList()
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
