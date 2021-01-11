package io.github.nickacpt.nickarcade.utils.interop

import io.github.nickacpt.nickarcade.utils.profiles.DumpedProfile
import io.github.nickacpt.nickarcade.utils.profiles.DumpedProperty
import net.minestom.server.entity.PlayerSkin
import java.util.*

data class ProfileProperty(var name: String, var value: String, var signature: String)

data class PlayerProfile(
    var uuid: UUID? = UUID(0, 0),
    var name: String? = "",
    val properties: MutableList<ProfileProperty> = mutableListOf()
) {
    fun toSkin(): PlayerSkin {
        val textures = properties.first { it.name == "textures" }
        return PlayerSkin(textures.value, textures.signature)
    }

    fun toDumpedProfile(): DumpedProfile {
        val name = name ?: ""
        val props = mutableMapOf<String, List<DumpedProperty>>()
        properties.map { DumpedProperty(it.name, it.value, it.signature) }.forEach {
            props[it.name] = listOf(it)
        }
        return DumpedProfile(tabShownName = name, name, name, uuid ?: UUID.randomUUID(), props)
    }
}
