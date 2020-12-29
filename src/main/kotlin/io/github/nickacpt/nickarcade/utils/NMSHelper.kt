package io.github.nickacpt.nickarcade.utils

import com.destroystokyo.paper.profile.PlayerProfile
import org.apache.commons.lang.reflect.FieldUtils.readField
import org.bukkit.Bukkit
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

val oldProfiles = mutableMapOf<UUID, PlayerProfile>()

