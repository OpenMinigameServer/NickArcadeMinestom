package io.github.nickacpt.nickarcade.utils.interop

import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.nickarcade.NickArcadeExtension
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.platform.minestom.MinestomComponentSerializer
import net.kyori.adventure.text.ComponentLike
import net.minestom.server.MinecraftServer
import net.minestom.server.chat.ChatParser.COLOR_CHAR
import net.minestom.server.chat.JsonMessage
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.regex.Pattern


object InteropExtensions {
    val legacyColorCodesMap = mutableMapOf<Char, MinecraftChatColor>()

    init {
        legacyColorCodesMap.set('k', MinecraftChatColor.OBFUSCATED)
        legacyColorCodesMap.set('l', MinecraftChatColor.BOLD)
        legacyColorCodesMap.set('m', MinecraftChatColor.STRIKETHROUGH)
        legacyColorCodesMap.set('n', MinecraftChatColor.UNDERLINED)
        legacyColorCodesMap.set('o', MinecraftChatColor.ITALIC)
        legacyColorCodesMap.set('r', MinecraftChatColor.RESET)
        legacyColorCodesMap.set('0', MinecraftChatColor.BLACK)
        legacyColorCodesMap.set('1', MinecraftChatColor.DARK_BLUE)
        legacyColorCodesMap.set('2', MinecraftChatColor.DARK_GREEN)
        legacyColorCodesMap.set('3', MinecraftChatColor.DARK_AQUA)
        legacyColorCodesMap.set('4', MinecraftChatColor.DARK_RED)
        legacyColorCodesMap.set('5', MinecraftChatColor.DARK_PURPLE)
        legacyColorCodesMap.set('6', MinecraftChatColor.GOLD)
        legacyColorCodesMap.set('7', MinecraftChatColor.GRAY)
        legacyColorCodesMap.set('8', MinecraftChatColor.DARK_GRAY)
        legacyColorCodesMap.set('9', MinecraftChatColor.BLUE)
        legacyColorCodesMap.set('a', MinecraftChatColor.GREEN)
        legacyColorCodesMap.set('b', MinecraftChatColor.AQUA)
        legacyColorCodesMap.set('c', MinecraftChatColor.RED)
        legacyColorCodesMap.set('d', MinecraftChatColor.LIGHT_PURPLE)
        legacyColorCodesMap.set('e', MinecraftChatColor.YELLOW)
        legacyColorCodesMap.set('f', MinecraftChatColor.WHITE)
    }
}

fun ComponentLike.toNative(): JsonMessage {
    return MinestomComponentSerializer.get().serialize(this.asComponent())
}


private val STRIP_COLOR_PATTERN: Pattern = Pattern.compile("(?i)$COLOR_CHAR[0-9A-FK-ORX]")

/**
 * Strips the given message of all color codes
 *
 * @param input String to strip of color
 * @return A copy of the input string, without any coloring
 */
@Contract("!null -> !null; null -> null")
fun stripColor(@Nullable input: String?): String {
    return if (input == null) {
        ""
    } else STRIP_COLOR_PATTERN.matcher(input).replaceAll("")
}

/**
 * Gets the ChatColors used at the end of the given input string.
 *
 * @param input Input string to retrieve the colors from.
 * @return Any remaining ChatColors to pass onto the next line.
 */
fun getLastColors(input: String): String {
    var result = ""
    val length = input.length

    // Search backwards from the end as it is faster
    for (index in length - 1 downTo -1 + 1) {
        val section = input[index]
        if (section == COLOR_CHAR && index < length - 1) {
            val c = input[index + 1]
            val color = InteropExtensions.legacyColorCodesMap[c] ?: MinecraftChatColor.RESET
            result = color.toString() + result

            // Once we find a color or reset we can stop searching
            if (!color.isSpecial || color == MinecraftChatColor.RESET) {
                break
            }
        }
    }
    return result
}

fun NickArcadeExtension.launch(block: suspend CoroutineScope.() -> Unit) {
    CoroutineSession.launch(MinestomCoroutineDispatcher, block)
}

fun NickArcadeExtension.async(block: suspend CoroutineScope.() -> Unit) {
    CoroutineSession.launch(AsyncCoroutineDispatcher, block)
}

fun getPlayer(name: String): Player? {
    return MinecraftServer.getConnectionManager().getPlayer(name)
}

fun getPlayer(id: UUID): Player? {
    return MinecraftServer.getConnectionManager().getPlayer(id)
}

fun getOnlinePlayers(): Collection<Player> {
    return MinecraftServer.getConnectionManager().onlinePlayers
}

@JvmName("selfCallEvent")
inline fun <reified T : Event> T.callEvent() {
    callEvent(this)
}

inline fun <reified T : Event> callEvent(event: T) {
    MinecraftServer.getGlobalEventHandler().callEvent(T::class.java, event)
}

val Player.uniqueId: UUID
    get() = uuid

val Player.name: String
    get() = username
val logger: Logger = LoggerFactory.getLogger("NickArcade")