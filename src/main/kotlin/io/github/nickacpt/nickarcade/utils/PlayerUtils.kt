package io.github.nickacpt.nickarcade.utils

import com.destroystokyo.paper.profile.PlayerProfile
import io.github.nickacpt.nickarcade.data.getPlayerData
import org.bukkit.entity.Player
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

var Player.actualPlayerProfile: PlayerProfile?
    get() = playerProfile
    set(value) {
        if (!oldProfiles.containsKey(uniqueId))
            oldProfiles[uniqueId] = playerProfile

        val finalProfile = value ?: oldProfiles[uniqueId]!!
        NMSHelper.replacePlayersByNameKey(profileName, finalProfile.name!!)
        this.playerProfile = finalProfile
    }
var Player.profileName: String
    get() = playerProfile.name ?: name
    set(value) {
        NMSHelper.replacePlayersByNameKey(profileName, value)
        playerProfile = playerProfile.also { it.name = value }
    }