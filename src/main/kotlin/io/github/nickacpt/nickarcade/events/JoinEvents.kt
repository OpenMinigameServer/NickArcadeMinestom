package io.github.nickacpt.nickarcade.events

import com.github.shynixn.mccoroutine.launch
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.PlayerDataManager
import io.github.nickacpt.nickarcade.data.getPlayerData
import io.github.nickacpt.nickarcade.utils.*
import kotlinx.coroutines.delay
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.time.seconds

fun registerJoinEvents() {

    event<PlayerJoinEvent> {
        joinMessage = null

        sendPlayerDataActionBar()

        val playerData = async {
            player.getPlayerData()
        }

        val superStarColors = listOf(ChatColor.AQUA, ChatColor.RED, ChatColor.GREEN)
        val joinPrefix =
            if (playerData.hasAtLeastRank(HypixelPackageRank.SUPERSTAR)) " ${superStarColors.joinToString("") { "$it>" }} " else ""
        val joinSuffix = if (playerData.hasAtLeastRank(HypixelPackageRank.SUPERSTAR)) " ${
            superStarColors.reversed().joinToString("") { "$it<" }
        } " else ""
        Bukkit.broadcastMessage("$joinPrefix${playerData.getChatName()}§6 joined the lobby!$joinSuffix")

        /*object : TimerBase(30.seconds) {
            override suspend fun onTick(duration: Duration, scope: CoroutineScope) {
                player.sendMessage("Time remaining: ${(30.seconds - duration).inSeconds.roundToInt()} seconds")
            }
        }.apply { start() }*/
    }

    event<AsyncPlayerChatEvent>
    {
        isCancelled = true
        val playerData = this.player.getPlayerData()
        if (!playerData.hasAtLeastRank(HypixelPackageRank.VIP) && !player.cooldown("chat", 3.seconds)) {
            player.asAudience.sendMessage(
                text {
                    it.append(
                        text(
                            "You can only chat once every 3 seconds! Ranked users bypass this restriction!",
                            NamedTextColor.RED
                        )
                    )
                }
            )

            return@event
        }
        val message = playerData.formatChatMessage(message)
        recipients.forEach { p ->
            p.asAudience.sendMessage(
                Identity.identity(player.uniqueId),
                text(message)
                    .hoverEvent(
                        text {
                            it.run {
                                append(text(playerData.getChatName()))
                                append(newline())
                                append(text("Hypixel Level: ", NamedTextColor.GRAY))
                                append(text(playerData.hypixelData?.networkLevel ?: 0, NamedTextColor.GOLD))
                            }
                        }
                    )
            )
        }
    }
}

private fun PlayerJoinEvent.sendPlayerDataActionBar() {
    pluginInstance.launch {
        val audience = player.asAudience
        while (!PlayerDataManager.isPlayerDataLoaded(player.uniqueId)) {
            audience.sendActionBar(
                text(
                    "Fetching player data from Hypixel, please wait!",
                    NamedTextColor.RED,
                    TextDecoration.BOLD
                )
            )
            delay(5.ticks)
        }
        audience.sendActionBar(text("Player data fetched from Hypixel! Have a nice stay.", NamedTextColor.GREEN))
    }
}

fun registerLeaveEvents() {
    event<PlayerQuitEvent> {
        PlayerDataManager.saveAndRemovePlayerData(player.uniqueId)
    }
}