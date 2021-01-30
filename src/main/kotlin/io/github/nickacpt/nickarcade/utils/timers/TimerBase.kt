package io.github.nickacpt.nickarcade.utils.timers

import io.github.nickacpt.nickarcade.NickArcadeExtension
import io.github.nickacpt.nickarcade.utils.interop.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.seconds


abstract class TimerBase constructor(private val duration: Duration, private val stepTime: Duration = 1.seconds) {
    abstract suspend fun onTick(duration: Duration, scope: CoroutineScope)

    var elapsedTime: Duration = Duration.ZERO
    private val isRunning get() = elapsedTime < duration
    var isPaused = false
    private var hasStarted = false

    fun restart() {
        stop()
        isPaused = false
        start()
    }

    fun stop() {
        isPaused = true
        elapsedTime = Duration.ZERO
    }

    fun start() {
        if (hasStarted) return
        NickArcadeExtension.instance.launch {
            hasStarted = true
            while (isRunning) {
                delay(stepTime)
                if (!isPaused) {
                    onTick(elapsedTime, this)
                    elapsedTime += stepTime
                }
            }
        }
    }
}