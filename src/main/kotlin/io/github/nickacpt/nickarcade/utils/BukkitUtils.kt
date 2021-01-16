@file:Suppress("UnstableApiUsage")

package io.github.nickacpt.nickarcade.utils

import cloud.commandframework.Command
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.NickArcadeExtension
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.utils.debugsubjects.DebugSubjectPlayer
import io.github.nickacpt.nickarcade.utils.interop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.AudienceProvider
import net.kyori.adventure.platform.minestom.MinestomAudiences
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.event.CancellableEvent
import net.minestom.server.event.Event
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent

fun command(
    sender: ArcadeSender,
    requiredRank: HypixelPackageRank = HypixelPackageRank.NONE,
    block: suspend CoroutineScope.() -> Unit
) {
    command(sender.commandSender, requiredRank, block)
}

fun command(
    sender: CommandSender,
    requiredRank: HypixelPackageRank = HypixelPackageRank.NONE,
    block: suspend CoroutineScope.() -> Unit
) {
    runBlocking {
        val isPlayer = sender is Player
        val rank = requiredRank.name.toLowerCase()
        val requiresPermission = requiredRank != HypixelPackageRank.NONE
        val hasPermission = !isPlayer || (sender as Player).hasPermission(rank)
        if (requiresPermission && !hasPermission) {
            sender.asAudience.sendMessage(text("You must be $rank or higher to use this command!", NamedTextColor.RED))
            return@runBlocking
        }
        pluginInstance.launch(block)
    }
}

val pluginInstance get() = NickArcadeExtension.instance
val minestomAudiences by lazy { MinestomAudiences.create() }

val CommandSender.asAudience
    get() =
        if (this is DebugSubjectPlayer) this.target.asRedirectAudience(name) else
            minestomAudiences.sender(this)

suspend inline fun <T> async(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncCoroutineDispatcher, block)

suspend inline fun <T> sync(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(MinestomCoroutineDispatcher, block)


@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> event(
    ignoreCancelled: Boolean = false, forceAsync: Boolean = false, forceBlocking: Boolean = false,
    noinline code: suspend T.(CoroutineScope) -> Unit
) {
    MinecraftServer.getGlobalEventHandler().addEventCallback(T::class.java) {
        val block: suspend CoroutineScope.() -> Unit = scope@{
            if (it is CancellableEvent && ignoreCancelled && it.isCancelled) return@scope
            code(it, this)
        }
        if (forceBlocking || it is AsyncPlayerPreLoginEvent) {
            runBlocking { block(this) }
            return@addEventCallback
        }
        if (forceAsync) {
            pluginInstance.async(block)
        } else {
            pluginInstance.launch(block)
        }
    }
}


@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> cancelEvent(
    forceAsync: Boolean = false,
    noinline code: suspend T.(CoroutineScope) -> Unit
) {
    MinecraftServer.getGlobalEventHandler().addEventCallback(T::class.java) {
        if (it is CancellableEvent) it.isCancelled = true

        val block: suspend CoroutineScope.() -> Unit = scope@{
            code(it, this)
        }
        if (forceAsync)
            pluginInstance.async(block)
        else
            pluginInstance.launch(block)
    }
}


/**
 * Creates an audience based on a viewer predicate.
 *
 * @param predicate a predicate
 * @return an audience
 * @since 4.0.0
 */
suspend fun AudienceProvider.filterSuspend(predicate: suspend (CommandSender) -> Boolean): Audience {
    val list = MinecraftServer.getConnectionManager().onlinePlayers + MinecraftServer.getCommandManager().consoleSender
    return Audience.audience(list.asFlow().filter(predicate).map {
        it.asAudience
    }.toCollection(mutableListOf()))
}

fun <C> Command.Builder<C>.permission(rank: HypixelPackageRank): Command.Builder<C> {
    return this.permission(rank.name.toLowerCase())
}