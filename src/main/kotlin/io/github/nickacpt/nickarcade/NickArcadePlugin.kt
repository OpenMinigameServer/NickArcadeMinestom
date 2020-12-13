package io.github.nickacpt.nickarcade

import com.destroystokyo.paper.MaterialTags
import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import io.github.nickacpt.hypixelapi.HypixelService
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.hypixelapi.utis.HypixelPlayerInfoHelper
import io.github.nickacpt.nickarcade.commands.RankCommands
import io.github.nickacpt.nickarcade.commands.TestCommands
import io.github.nickacpt.nickarcade.data.MongoDbConnectionHelper
import io.github.nickacpt.nickarcade.data.config.MainConfigurationFile
import io.github.nickacpt.nickarcade.events.registerJoinEvents
import io.github.nickacpt.nickarcade.events.registerLeaveEvents
import io.github.nickacpt.nickarcade.utils.commands.NickArcadeCommandManager
import io.github.nickacpt.nickarcade.utils.config.ArcadeConfigurationFile
import io.github.nickacpt.nickarcade.utils.event
import io.github.nickacpt.nickarcade.utils.scope
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.*

class NickArcadePlugin : JavaPlugin() {

    private lateinit var databaseClient: CoroutineClient
    lateinit var database: CoroutineDatabase
    private lateinit var commandManager: NickArcadeCommandManager

    companion object {
        lateinit var instance: NickArcadePlugin
    }

    lateinit var service: HypixelService
    lateinit var hypixelPlayerInfoHelper: HypixelPlayerInfoHelper

    fun walterProfile(id: UUID, name: String): PlayerProfile {
        return Bukkit.createProfile(id, name).apply {
            properties.add(
                ProfileProperty(
                    "textures",
                    "ewogICJ0aW1lc3RhbXAiIDogMTU5NjY1MzIyMTE5MiwKICAicHJvZmlsZUlkIiA6ICI0MjZhMDcyMTU0MjA0OTI1YTBjZTBhZTJkYzI2N2NlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ3bHRlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hOGMwZmMyN2FjMjE4NWI2NTIwMDdhNjlhZjkyMDBjNDgyYzQ2NDM1NWZlMTkwZTU2NjJhNWU2ODQxMThiNjQxIgogICAgfQogIH0KfQ==",
                    "tUMqCZNARmvO0r2kjpC6Udknv55op5XLiDfKEJ4dLa5P7g16utxSs7dUvcVkYxvlwO1JA9OmFqOhtV9M2nMA1YK65aiADYQfp0DW7t+lV27jy2AwwXR/IVP5+xpjfG4tCCe+hTiSRkVRzmQLEK90Wh99EW1UmHgXSq7fJSacyvVi9K1ycQFtR1Z3D+PnqJmlypEjnzs5rcX+oUF9W7QS/PJWWm/AEUqtMLZ88fwi9Jfku/RHnK+KaQsPFHI7D8NcEB1jzh0fn4EVsLB+UEOuNmhusyQcPRPSN6n9tG2d7GsdzOKq+9Mj61/blm/KEHzUfVQ3Rr+jyZPXS3+5wwnbgZPRVW8pO2e/k8tVbFt4mLyNacM5gBOONbol6x/39/X7VL6A/TrzDzWcN1RhT0fCWiRjrqqUH98u+aJbHuhJQRe8qz4cPGIa/Gx1bSOWz6aUuBj66u7EAcx0h5qluMTJ5w16woWaR4omIa4lCA5DSV4X116fCzAwCg2vzMAWxQoWag2Mg3nqLcmrhKJaObEtTLwArLERhZm48qx9oTj9ENd3WyXqJmQM2WTw/6uezv90vGIqux4Z9VaHS97GQLUEjwFGQkKXOPxa74Mc5GvUm6zua7KQQkH7+WZ9pfIhlhWbJZHNBr9TB/utfWpz15ZP2Q++gQg3eYyVZ8H2t5Ab1EA="
                )
            )
        }
    }

    override fun onEnable() {
        instance = this
        commandManager = NickArcadeCommandManager(this).init() ?: disablePlugin("Unable to initialize command manager")

        initMainConfig()
        initHypixelServices() ?: disablePlugin("Unable to initialize Hypixel services")

        commandManager.registerCommands()

        registerJoinEvents()
        registerLeaveEvents()

        //registerFireballEvents()
    }

    private fun disablePlugin(reason: String): Nothing {
        server.pluginManager.disablePlugin(this)
        throw Exception("Unable to initialize plugin! Reason: $reason")
    }

    private fun initHypixelServices(): Unit? {
        if (mainConfiguration.hypixelKey != UUID(0, 0)) {
            service = HypixelApi.getService(mainConfiguration.hypixelKey)
            hypixelPlayerInfoHelper = HypixelPlayerInfoHelper(service)
            return Unit
        }
        return null
    }

    lateinit var mainConfiguration: MainConfigurationFile

    private fun initMainConfig() {
        mainConfiguration = ArcadeConfigurationFile("config.yml").load()
        connectMongoDb()
    }

    private fun connectMongoDb() {
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )
        databaseClient = mainConfiguration.mongoDbConfiguration.let {
            MongoDbConnectionHelper.buildClient(it.host, it.port, it.username, it.database, it.password)
        }
        database = databaseClient.getDatabase(mainConfiguration.mongoDbConfiguration.database)
    }

    override fun onDisable() {
    }
}

fun registerFireballEvents() {

    event<EntityExplodeEvent> {
        if (this.entityType != EntityType.FIREBALL) return@event
        blockList().removeIf { MaterialTags.STAINED_GLASS.isTagged(it) }
    }

    event<PlayerInteractEvent> {
        if ((action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)) return@event
        this.item?.takeIf { it.type == Material.FIRE_CHARGE } ?: return@event
        player.scope("fireball") {
            isCancelled = true
            player.launchProjectile(Fireball::class.java).apply {
                `yield` = 2.0f
                direction = player.eyeLocation.direction
                velocity = direction.multiply(5)
            }
        }
    }
}

private fun NickArcadeCommandManager.registerCommands() {
    annotationParser.parse(RankCommands)
    annotationParser.parse(TestCommands)
}
