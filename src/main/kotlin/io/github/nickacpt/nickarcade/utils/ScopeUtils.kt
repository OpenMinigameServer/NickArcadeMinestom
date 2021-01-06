package io.github.nickacpt.nickarcade.utils

import net.minestom.server.entity.Player


fun Player.scope(name: String, code: () -> Unit) {
    val data = this.data ?: return
    if (data.hasKey(name)) return
    val metadataKey = "scope-$name"
    data.set(metadataKey, true)
    code()
    data.set(metadataKey, null)
}