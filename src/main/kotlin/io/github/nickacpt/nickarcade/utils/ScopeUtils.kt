package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.nickarcade.data.getPlayerData
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.Metadatable
import kotlin.time.Duration

suspend fun Player.cooldown(name: String, cooldownDuration: Duration, code: (suspend () -> Unit)? = null): Boolean {
    val playerData = this.getPlayerData()
    val lastUse = playerData.cooldowns[name]
    val finishTime = lastUse?.plus(cooldownDuration.inMilliseconds)
    return if (finishTime == null || System.currentTimeMillis() > finishTime) {
        playerData.cooldowns[name] = System.currentTimeMillis()
        code?.invoke()
        true
    } else false
}

fun Metadatable.scope(name: String, code: () -> Unit) {
    if (hasMetadata(name)) return
    val metadataKey = "scope-$name"
    this.setMetadata(metadataKey, FixedMetadataValue(pluginInstance, true))
    code()
    removeMetadata(metadataKey, pluginInstance)
}