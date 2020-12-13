package io.github.nickacpt.nickarcade.data.config

import com.mongodb.ServerAddress
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import java.util.*

const val CURRENT_CONFIG_VERSION = 2

@ConfigSerializable
data class MongoDbConfiguration(
    val host: String = "localhost",
    val port: Int = ServerAddress.defaultPort(),
    val database: String = "NickArcade",
    val username: String = "",
    val password: String = ""
)

@ConfigSerializable
data class MainConfigurationFile(
    @Comment("The API key used to fetch data off of the Hypixel Network")
    var hypixelKey: UUID = UUID(0, 0),

    @Comment("The configuration for the SQL configuration")
    var mongoDbConfiguration: MongoDbConfiguration = MongoDbConfiguration()
)