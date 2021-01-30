package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.utils.timers.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.time.Duration

class LobbyWaitTimerBase(val game: Game, time: Duration) : CountDownTimer(time) {
    override suspend fun onCountDownTick(duration: Duration, scope: CoroutineScope) {
        if (game.playerCount < game.arenaDefinition.minPlayers && !game.isDeveloperGame()) {
            game.debug("Lobby wait time: <requirements not met>")
            elapsedTime = Duration.ZERO
            return
        }
        val time = duration.inSeconds.toInt()
        game.debug("Lobby wait time: ${time}s")
        val shouldShowTime = time <= 5 || time == 15 || time == 10

        if (shouldShowTime)
            game.audience.sendMessage(Component.text {
                val color = when {
                    time == 15 -> {
                        NamedTextColor.GREEN
                    }
                    time >= 10 -> {
                        NamedTextColor.YELLOW
                    }
                    else -> {
                        NamedTextColor.RED
                    }
                }
                it.append(Component.text("The game starts in ", NamedTextColor.YELLOW))
                it.append(Component.text(time, color))
                it.append(Component.text(" second${if (time != 1) "s" else ""}!", NamedTextColor.YELLOW))
            })
    }

    override suspend fun onCountDownFinish(scope: CoroutineScope) {
        game.state = GameState.IN_GAME
    }
}