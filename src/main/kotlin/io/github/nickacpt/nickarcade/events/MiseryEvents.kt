package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.nickarcade.data.getPlayerData
import io.github.nickacpt.nickarcade.events.impl.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.utils.event
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.BLACK
import net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE
import net.kyori.adventure.text.format.TextDecoration.OBFUSCATED
import org.apache.commons.lang.reflect.FieldUtils
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random
import kotlin.time.minutes

inline fun <reified T : Event> cancelHalf(crossinline condition: T.() -> Boolean = { true }) {
    event<T>(eventPriority = EventPriority.LOWEST, ignoreCancelled = true) {
        if (this is Cancellable) {
            var myPlayer = (this as? PlayerEvent)?.player
            kotlin.runCatching {
                myPlayer = FieldUtils.readDeclaredField(this, "player", true) as Player?
            }
            kotlin.runCatching {
                myPlayer = FieldUtils.readDeclaredField(this, "damager", true) as Player?
            }

            myPlayer ?: return@event

            val playerData = myPlayer!!.getPlayerData()

            if (condition() && playerData.overrides.miseryMode == true && Random.nextBoolean()) {
                isCancelled = true
            }
        }
    }
}

fun registerMiseryEvents() {
    cancelHalf<BlockPlaceEvent>()
    cancelHalf<BlockMultiPlaceEvent>()
    cancelHalf<BlockBreakEvent>()
    cancelHalf<EntityDamageByEntityEvent> { this.damager is Player }

    event<PlayerDataJoinEvent> {
        val bukkitPlayer = player.player ?: return@event

        if (player.overrides.miseryMode != true) {
            bukkitPlayer.removePotionEffect(PotionEffectType.BLINDNESS)
            return@event
        }
        val audience = player.audience
        audience.sendMessage(text {
            it.append(text("You suddenly feel a heavy weight on top of you.", DARK_PURPLE))
            it.append(newline())
            it.append(text("DIE ", BLACK, OBFUSCATED))
            it.append(text("NickAc will remember that.", BLACK))
            it.append(text(" DIE", BLACK, OBFUSCATED))
        })

        bukkitPlayer.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Int.MAX_VALUE, 100, true, false, false))
        bukkitPlayer.playSound(bukkitPlayer.location, org.bukkit.Sound.ENTITY_BAT_DEATH, SoundCategory.MASTER, 1f, 1f)

        while (bukkitPlayer.isOnline) {
            delay(Random.nextInt(2, 5).minutes)

            bukkitPlayer.playSound(
                bukkitPlayer.location,
                org.bukkit.Sound.ENTITY_BAT_DEATH,
                SoundCategory.MASTER,
                1f,
                1f
            )
        }

    }
}