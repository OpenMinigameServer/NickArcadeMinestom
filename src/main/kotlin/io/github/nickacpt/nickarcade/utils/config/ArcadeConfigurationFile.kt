package io.github.nickacpt.nickarcade.utils.config

import io.github.nickacpt.nickarcade.data.config.CURRENT_CONFIG_VERSION
import io.github.nickacpt.nickarcade.utils.pluginInstance
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.objectMapper
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File

inline class ArcadeConfigurationFile(private val fileName: String) {
    val file get() = File(pluginInstance.dataFolder, fileName)
    val loader: YamlConfigurationLoader
        get() = YamlConfigurationLoader.builder()
            .defaultOptions { opts: ConfigurationOptions ->
                opts.shouldCopyDefaults(true)
            }
            .nodeStyle(NodeStyle.BLOCK)
            .file(file)
            .build()

    inline fun <reified T> load(): T {
        val node: CommentedConfigurationNode = loader.load()
        val objectMapper = objectMapper<T>()
        val isVersionNotSynced = prepareNodeConfig(node)

        return objectMapper.load(node).also {
            if (!file.exists() || isVersionNotSynced) {
                loader.save(node)
            }
        }
    }

    fun prepareNodeConfig(node: CommentedConfigurationNode): Boolean {
        val configVersionNode = node.node("config_version")
        val configVersion = configVersionNode?.int ?: 0

        val isVersionNotSynced = configVersion < CURRENT_CONFIG_VERSION
        isVersionNotSynced.takeIf { true }?.also { configVersionNode.set(CURRENT_CONFIG_VERSION) }
        return isVersionNotSynced
    }

    inline fun <reified T> save(value: T) {
        val node: CommentedConfigurationNode = loader.load()
        objectMapper<T>().save(value, node)
        prepareNodeConfig(node)
        loader.save(node)
    }
}