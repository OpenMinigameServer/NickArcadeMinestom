package io.github.nickacpt.hypixelapi.utis

import kotlin.math.floor
import kotlin.math.sqrt

interface NetworkLeveling {
    companion object {
        /**
         * This method returns the level of a player calculated by the current experience gathered. The result is
         * a precise level of the player The value is not zero-indexed and represents the absolute visible level
         * for the player.
         * The result can't be smaller than 1 and negative experience results in level 1.
         *
         *
         * Examples:
         * -        0 XP = 1.0
         * -     5000 XP = 1.0
         * -    10000 XP = 2.0
         * -    50000 XP = 4.0
         * - 79342431 XP = 249.0
         *
         * @param exp Total experience gathered by the player.
         * @return Absolute level of player (Smallest value is 1.0)
         */
        fun getLevel(exp: Double): Double {
            return if (exp < 0) 1.0 else floor(1 + REVERSE_PQ_PREFIX + sqrt(REVERSE_CONST + GROWTH_DIVIDES_2 * exp))
        }

        /**
         * This method returns the level of a player calculated by the current experience gathered. The result is
         * a precise level of the player The value is not zero-indexed and represents the visible level
         * for the player.
         * The result can't be smaller than 1 and negative experience results in level 1.
         *
         *
         * Examples:
         * -        0 XP = 1.0
         * -     5000 XP = 1.5
         * -    10000 XP = 2.0
         * -    50000 XP = 4.71...
         * - 79342431 XP = 249.46...
         *
         * @param exp Total experience gathered by the player.
         * @return Exact level of player (Smallest value is 1.0)
         */
        fun getExactLevel(exp: Double): Double {
            return getLevel(exp) + getPercentageToNextLevel(exp)
        }

        /**
         * This method returns the amount of experience that is needed to progress from level to level + 1. (5 to 6)
         * The levels passed must absolute levels with the smallest level being 1. Smaller values always return
         * the BASE constant. The calculation is precise and if a decimal is passed it returns the XP from the
         * progress of this level to the next level with the same progress. (5.5 to 6.5)
         *
         *
         * Examples:
         * -   1 (to 2)   =  10000.0 XP
         * -   2 (to 3)   =  12500.0 XP
         * -   3 (to 4)   =  15000.0 XP
         * -   5 (to 6)   =  20000.0 XP
         * - 5.5 (to 6.5) =  21250.0 XP
         * - 130 (to 131) = 332500.0 XP
         * - 250 (to 251) = 632500.0 XP
         *
         * @param level Level from which you want to get the next level with the same level progress
         * @return Experience to reach the next level with same progress
         */
        fun getExpFromLevelToNext(level: Double): Double {
            return if (level < 1) BASE else GROWTH * (level - 1) + BASE
        }

        /**
         * This method returns the experience it needs to reach that level. If you want to reach the given level
         * you have to gather the amount of experience returned by this method. This method is precise, that means
         * you can pass any progress of a level to receive the experience to reach that progress. (5.764 to get
         * the experience to reach level 5 with 76.4% of level 6.
         *
         *
         * Examples:
         * -    1.0 =        0.0 XP
         * -    2.0 =    10000.0 XP
         * -    3.0 =    22500.0 XP
         * -    5.0 =    55000.0 XP
         * -  5.764 =    70280.0 XP
         * -  130.0 = 21930000.0 XP
         * - 250.43 = 79951975.0 XP
         *
         * @param level The level and progress of the level to reach
         * @return The experience required to reach that level and progress
         */
        fun getTotalExpToLevel(level: Double): Double {
            val lv = floor(level)
            val x0 = getTotalExpToFullLevel(lv)
            return if (level == lv) x0 else (getTotalExpToFullLevel(lv + 1) - x0) * (level % 1) + x0
        }

        /**
         * Helper method that may only be called by full levels and has the same functionality as getTotalExpToLevel()
         * but doesn't support progress and returns wrong values for progress due to perfect curve shape.
         *
         * @param level Level to receive the amount of experience to
         * @return Experience to reach the given level
         */
        fun getTotalExpToFullLevel(level: Double): Double {
            return (HALF_GROWTH * (level - 2) + BASE) * (level - 1)
        }

        /**
         * This method returns the current progress of this level to reach the next level. This method is as
         * precise as possible due to rounding errors on the mantissa. The first 10 decimals are totally
         * accurate.
         *
         *
         * Examples:
         * -     5000.0 XP   (Lv. 1) = 0.5                               (50 %)
         * -    22499.0 XP   (Lv. 2) = 0.99992                       (99.992 %)
         * -  5324224.0 XP  (Lv. 62) = 0.856763076923077   (85.6763076923077 %)
         * - 23422443.0 XP (Lv. 134) = 0.4304905109489051 (43.04905109489051 %)
         *
         * @param exp Current experience gathered by the player
         * @return Current progress to the next level
         */
        fun getPercentageToNextLevel(exp: Double): Double {
            val lv = getLevel(exp)
            val x0 = getTotalExpToLevel(lv)
            return (exp - x0) / (getTotalExpToLevel(lv + 1) - x0)
        }

        const val EXP_FIELD = "networkExp"
        const val LVL_FIELD = "networkLevel"
        const val BASE = 10000.0
        const val GROWTH = 2500.0

        /* Constants to generate the total amount of XP to complete a level */
        const val HALF_GROWTH = 0.5 * GROWTH

        /* Constants to look up the level from the total amount of XP */
        const val REVERSE_PQ_PREFIX = -(BASE - 0.5 * GROWTH) / GROWTH
        const val REVERSE_CONST = REVERSE_PQ_PREFIX * REVERSE_PQ_PREFIX
        const val GROWTH_DIVIDES_2 = 2 / GROWTH
    }
}