package io.github.nickacpt.nickarcade.game

import io.github.nickacpt.nickarcade.utils.timers.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.sound.Sound
import net.minestom.server.sound.SoundCategory
import kotlin.time.Duration

class LobbyWaitTimerBase(val game: Game, time: Duration) : CountDownTimer(time) {
    val isWaiting: Boolean
        get() = game.playerCount < game.arenaDefinition.minPlayers && !game.isDeveloperGame()

    override suspend fun onCountDownTick(duration: Duration, scope: CoroutineScope) {
        if (isWaiting) {
            game.debug("Lobby wait time: <requirements not met>")
            elapsedTime = Duration.ZERO
            game.refreshMemberScoreboards()
            return
        }
        val time = duration.inSeconds.toInt()
        game.debug("Lobby wait time: ${time}s")
        val shouldShowTime = time <= 5 || time == 15 || time == 10

        if (shouldShowTime) {
            game.members.mapNotNull { it.player }
                .forEach { it.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.BLOCKS, 20f, 1f) }
            game.audience.sendMessage(Component.text {
                val color = when {
                    time == 15 -> {
                        NamedTextColor.GREEN
                    }
                    time >= 10 -> {
                        NamedTextColor.GOLD
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

        game.refreshMemberScoreboards()
    }

    override suspend fun onCountDownFinish(scope: CoroutineScope) {
        game.state = GameState.IN_GAME
    }
}