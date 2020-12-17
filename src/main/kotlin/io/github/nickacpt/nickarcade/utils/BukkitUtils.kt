@file:Suppress("UnstableApiUsage")

package io.github.nickacpt.nickarcade.utils

import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.minecraftDispatcher
import com.google.common.reflect.TypeToken
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.NickArcadePlugin
import io.github.nickacpt.nickarcade.data.getPlayerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventException
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.lang.reflect.InvocationTargetException

fun command(
    sender: CommandSender,
    requiredRank: HypixelPackageRank = HypixelPackageRank.NONE,
    block: suspend CoroutineScope.() -> Unit
) {
    runBlocking {
        val isPlayer = sender is Player
        val rank = requiredRank.name.toLowerCase()
        val requiresPermission = requiredRank != HypixelPackageRank.NONE
        val hasPermission = !isPlayer || (sender as Player).getPlayerData().hasAtLeastRank(requiredRank)
        if (requiresPermission && !hasPermission) {
            sender.sendMessage(ChatColor.RED.toString() + "You must be $rank or higher to use this command!")
            return@runBlocking
        }
        pluginInstance.launch(block)
    }
}

val pluginInstance get() = NickArcadePlugin.instance
val bukkitAudiences by lazy { BukkitAudiences.create(pluginInstance) }

val CommandSender.asAudience get() = bukkitAudiences.sender(this)

suspend inline fun <T> async(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(pluginInstance.asyncDispatcher, block)

suspend inline fun <T> sync(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(pluginInstance.minecraftDispatcher, block)


@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> event(
    eventPriority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false, forceAsync: Boolean = false,
    noinline code: suspend T.(CoroutineScope) -> Unit
) {
    val type = object : TypeToken<T>() {}
    pluginInstance.server.pluginManager.registerEvent(
        type.rawType as Class<out Event>, object : Listener {}, eventPriority,
        { _, event ->
            if (!T::class.java.isInstance(event)) return@registerEvent
            try {
                val isAsync = forceAsync || event.isAsynchronous
                if (isAsync) {
                    pluginInstance.launchAsync { code(event as T, this) }
                } else {
                    pluginInstance.launch { code(event as T, this) }
                }
            } catch (var4: InvocationTargetException) {
                throw EventException(var4.cause)
            } catch (var5: Throwable) {
                throw EventException(var5)
            }
        },
        pluginInstance, ignoreCancelled
    )
}