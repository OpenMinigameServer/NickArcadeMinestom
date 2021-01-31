package io.github.nickacpt.nickarcade.scoreboard

import net.kyori.adventure.text.Component

data class SidebarData(val title: Component, val lines: Array<Component>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SidebarData

        if (title != other.title) return false
        if (!lines.contentEquals(other.lines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + lines.contentHashCode()
        return result
    }
}
