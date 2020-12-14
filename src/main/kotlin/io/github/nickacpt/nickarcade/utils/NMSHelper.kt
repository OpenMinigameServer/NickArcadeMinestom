package io.github.nickacpt.nickarcade.utils

import com.destroystokyo.paper.profile.PlayerProfile
import org.apache.commons.lang.reflect.FieldUtils.readField
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object NMSHelper {
    private val playerList = readField(Bukkit.getServer(), "playerList", true)

    @Suppress("UNCHECKED_CAST")
    private val playersByName = readField(playerList, "playersByName", true)
            as MutableMap<String, Any>

    fun replacePlayersByNameKey(key: String, newKey: String) {
        val oldValue = playersByName[key.toLowerCase()] ?: return
        playersByName[newKey.toLowerCase()] = oldValue
        playersByName.remove(key.toLowerCase())
    }
}

var Player.profileName: String
    get() = playerProfile.name ?: name
    set(value) {
        NMSHelper.replacePlayersByNameKey(profileName, value)
        playerProfile = playerProfile.also { it.name = value }
    }

var Player.actualPlayerProfile: PlayerProfile
    get() = playerProfile
    set(value) {
        NMSHelper.replacePlayersByNameKey(profileName, value.name!!)
        this.playerProfile = value
    }