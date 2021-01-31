package io.github.nickacpt.nickarcade.utils.timers

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.seconds

abstract class CountDownTimer(private val duration: Duration, stepTime: Duration = 1.seconds) :
    TimerBase(duration + 2.seconds, stepTime) {
    abstract suspend fun onCountDownTick(duration: Duration, scope: CoroutineScope)
    abstract suspend fun onCountDownFinish(scope: CoroutineScope)

    val remainingDuration: Duration
        get() = (this.duration - elapsedTime).coerceAtLeast(0.seconds)

    override suspend fun onTick(duration: Duration, scope: CoroutineScope) {
        if (remainingDuration != Duration.ZERO) {
            onCountDownTick(remainingDuration, scope)
        } else {
            onCountDownFinish(scope)
        }
    }
}