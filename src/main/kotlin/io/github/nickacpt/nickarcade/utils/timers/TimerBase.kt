package io.github.nickacpt.nickarcade.utils.timers

import com.github.shynixn.mccoroutine.launch
import io.github.nickacpt.nickarcade.NickArcadePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.seconds


abstract class TimerBase constructor(private val duration: Duration, val stepTime: Duration = 1.seconds) {
    abstract suspend fun onTick(duration: Duration, scope: CoroutineScope)

    var elapsedTime: Duration = Duration.ZERO
    private val isRunning get() = elapsedTime < duration

    fun start() {
        NickArcadePlugin.instance.launch {
            while (isRunning) {
                onTick(elapsedTime, this)
                delay(stepTime)
                elapsedTime += stepTime
            }
        }
    }
}