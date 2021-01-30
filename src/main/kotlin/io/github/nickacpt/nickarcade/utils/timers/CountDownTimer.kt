package io.github.nickacpt.nickarcade.utils.timers

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.seconds

abstract class CountDownTimer(private val duration: Duration, stepTime: Duration = 1.seconds) :
    TimerBase(duration + 1.seconds, stepTime) {
    abstract suspend fun onCountDownTick(duration: Duration, scope: CoroutineScope)
    abstract suspend fun onCountDownFinish(scope: CoroutineScope)

    override suspend fun onTick(duration: Duration, scope: CoroutineScope) {
        val finalDuration = (this.duration - duration).coerceAtLeast(0.seconds)
        if (finalDuration != Duration.ZERO) {
            onCountDownTick(finalDuration, scope)
        } else {
            onCountDownFinish(scope)
        }
    }
}