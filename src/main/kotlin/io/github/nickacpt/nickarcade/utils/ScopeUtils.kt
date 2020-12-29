package io.github.nickacpt.nickarcade.utils

import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.Metadatable

fun Metadatable.scope(name: String, code: () -> Unit) {
    if (hasMetadata(name)) return
    val metadataKey = "scope-$name"
    this.setMetadata(metadataKey, FixedMetadataValue(pluginInstance, true))
    code()
    removeMetadata(metadataKey, pluginInstance)
}