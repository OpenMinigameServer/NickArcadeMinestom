package io.github.nickacpt.nickarcade.utils

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds


/** Returns a [Duration] equal to this [Int] number of ticks. */
@SinceKotlin("1.3")
@ExperimentalTime
val Int.ticks
    get() = (this * 50).milliseconds

/** Returns a [Duration] equal to this [Long] number of ticks. */
@SinceKotlin("1.3")
@ExperimentalTime
val Long.ticks
    get() = (this * 50L).milliseconds

/** Returns a [Duration] equal to this [Double] number of ticks. */
@SinceKotlin("1.3")
@ExperimentalTime
val Double.ticks
    get() = (this * 50.0).milliseconds