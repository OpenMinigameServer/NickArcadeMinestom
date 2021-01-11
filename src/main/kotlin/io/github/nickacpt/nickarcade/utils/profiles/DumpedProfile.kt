package io.github.nickacpt.nickarcade.utils.profiles

import io.github.nickacpt.nickarcade.utils.interop.PlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.ProfileProperty
import java.util.*

data class DumpedProfile(
    val tabShownName: String = "",
    val displayName: String = "",
    val name: String,
    val uuid: UUID,
    val properties: Map<String, List<DumpedProperty>>
) {
    fun asPlayerProfile(id: UUID? = null): PlayerProfile {
        val profile = PlayerProfile(id ?: uuid, name)
        properties.values.firstOrNull()?.map { ProfileProperty(it.name, it.value, it.signature) }?.let {
            profile.properties.addAll(
                it
            )
        }
        return profile.apply { this.name = this@DumpedProfile.name }
    }
}
