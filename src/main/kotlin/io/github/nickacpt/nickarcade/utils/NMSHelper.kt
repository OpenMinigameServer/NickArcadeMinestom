package io.github.nickacpt.nickarcade.utils

import com.destroystokyo.paper.profile.PlayerProfile
import org.apache.commons.lang.reflect.FieldUtils.readField
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object NMSHelper {
    private val playerList = readField(Bukkit.getServer(), "playerList", true)

    @Suppress("UNCHECKED_CAST")
    private val playersByName = readField(playerList, "playersByName", true)
            as MutableMap<String, Any>

    fun replacePlayersByNameKey(key: String, newKey: String) {
        val oldKeyLc = key.toLowerCase()
        val newKeyLc = newKey.toLowerCase()
        val oldValue = playersByName[oldKeyLc] ?: return
        playersByName[newKeyLc] = oldValue
        if (oldKeyLc != newKeyLc)
            playersByName.remove(oldKeyLc)
    }
}

var Player.profileName: String
    get() = playerProfile.name ?: name
    set(value) {
        NMSHelper.replacePlayersByNameKey(profileName, value)
        playerProfile = playerProfile.also { it.name = value }
    }

val oldProfiles = mutableMapOf<UUID, PlayerProfile>()

var Player.actualPlayerProfile: PlayerProfile?
    get() = playerProfile
    set(value) {
        if (!oldProfiles.containsKey(uniqueId))
            oldProfiles[uniqueId] = playerProfile

        val finalProfile = value ?: oldProfiles[uniqueId]!!
        NMSHelper.replacePlayersByNameKey(profileName, finalProfile.name!!)
        this.playerProfile = finalProfile
    }