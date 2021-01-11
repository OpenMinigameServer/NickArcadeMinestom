package io.github.nickacpt.nickarcade.utils

import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.events.toPlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.PlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.ProfileProperty
import io.github.nickacpt.nickarcade.utils.interop.name
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import kotlinx.coroutines.runBlocking
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import org.apache.commons.lang3.reflect.FieldUtils
import java.util.*
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

val oldProfiles = mutableMapOf<UUID, PlayerProfile>()

var Player.playerProfile: PlayerProfile
    get() {
        val properties = ArrayList<ProfileProperty>()
        val skin = skin
        if (skin != null) {
            properties.add(ProfileProperty("textures", skin.textures, skin.signature))
        } else {
            runBlocking {
                kotlin.runCatching {
                    getPlayerProfile()?.properties?.let {
                        properties.addAll(it)
                    }
                }
            }
        }
        return PlayerProfile(uuid, username, properties)

    }
    set(value) {
        FieldUtils.writeDeclaredField(this, "username", value.name, true)
        val properties: List<ProfileProperty> = value.properties
        if (properties.isNotEmpty()) {
            val (_, value1, signature) = properties.stream().findFirst().get()
            skin = PlayerSkin(value1, signature)
        }
    }

suspend fun Player.getPlayerProfile() =
    ProfileApi.getProfileById(uuid)?.toPlayerProfile()

var Player.actualPlayerProfile: PlayerProfile?
    get() = playerProfile
    set(value) {
        if (!oldProfiles.containsKey(uniqueId))
            oldProfiles[uniqueId] = playerProfile

        val finalProfile = value ?: oldProfiles[uniqueId]!!
        this.playerProfile = finalProfile
    }
var Player.profileName: String
    get() = playerProfile.name ?: name
    set(value) {
        playerProfile = playerProfile.also { it.name = value }
    }