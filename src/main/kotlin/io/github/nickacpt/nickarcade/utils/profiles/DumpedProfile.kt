package io.github.nickacpt.nickarcade.utils.profiles

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import org.bukkit.Bukkit
import java.util.*

data class DumpedProfile(
    val tabShownName: String = "",
    val displayName: String = "",
    val name: String,
    val uuid: UUID,
    val properties: Map<String, List<DumpedProperty>>
) {
    fun asPlayerProfile(id: UUID): PlayerProfile {
        val profile = Bukkit.createProfile(id, name)
        profile.properties.addAll(
            properties.values.first().map { ProfileProperty(it.name, it.value, it.signature) })
        return profile.apply { this.name = this@DumpedProfile.name }
    }
}
