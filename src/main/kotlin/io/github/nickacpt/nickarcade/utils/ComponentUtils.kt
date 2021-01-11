package io.github.nickacpt.nickarcade.utils

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.Hidden
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.minestom.server.entity.Player
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.*

fun Component.clickEvent(handler: suspend Player.() -> Unit): @NonNull Component {
    val id = ComponentUtils.generateClickEvent(handler)
    return this.clickEvent(ClickEvent.runCommand("/nickarcade internal $id"))
}

object ComponentUtils {
    private val clickEvents = mutableMapOf<UUID, suspend (Player) -> Unit>()

    fun generateClickEvent(handler: suspend (Player) -> Unit): UUID {
        val id = UUID.randomUUID()
        clickEvents[id] = handler
        return id
    }

    @Hidden
    @CommandMethod("nickarcade internal <id>")
    fun clickEventExecutor(player: Player, @Argument("id") id: UUID) {
        val event = clickEvents[id] ?: return
        clickEvents.remove(id)
        command(player) {
            event(player)
        }
    }

}