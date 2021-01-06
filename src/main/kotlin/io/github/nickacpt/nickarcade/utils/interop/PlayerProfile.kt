package io.github.nickacpt.nickarcade.utils.interop

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
}
