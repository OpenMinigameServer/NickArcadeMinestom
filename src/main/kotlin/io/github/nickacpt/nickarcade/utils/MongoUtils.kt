package io.github.nickacpt.nickarcade.utils

import org.litote.kmongo.property.KPropertyPath
import kotlin.reflect.KProperty1

operator fun <T0, T1, T2, T3> KProperty1<T0, T1?>.div(p2: KProperty1<T2, T3?>): KProperty1<T0, T3?> =
    @Suppress("INVISIBLE_MEMBER")
    (KPropertyPath(this, p2))