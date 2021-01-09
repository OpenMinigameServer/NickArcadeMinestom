package io.github.nickacpt.nickarcade

import io.github.nickacpt.hypixelapi.HypixelService
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.hypixelapi.utis.HypixelPlayerInfoHelper
import io.github.nickacpt.nickarcade.chat.ChatChannelsManager
import io.github.nickacpt.nickarcade.commands.*
import io.github.nickacpt.nickarcade.data.MongoDbConnectionHelper
import io.github.nickacpt.nickarcade.data.config.MainConfigurationFile
import io.github.nickacpt.nickarcade.display.DisplayPacketHandler.registerDisplayPacketHandler
import io.github.nickacpt.nickarcade.events.*
import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import io.github.nickacpt.nickarcade.schematics.manager.SchematicName
import io.github.nickacpt.nickarcade.utils.commands.NickArcadeCommandHelper
import io.github.nickacpt.nickarcade.utils.config.ArcadeConfigurationFile
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.io.File
import java.util.*


class NickArcadeExtension : Extension() {
    val dataFolder
        get() = File(
            MinecraftServer.getExtensionManager().extensionFolder,
            this.javaClass.simpleName.removeSuffix("Extension")
        )

    private lateinit var databaseClient: CoroutineClient
    lateinit var database: CoroutineDatabase
    internal lateinit var commandManager: NickArcadeCommandHelper

    companion object {
        lateinit var instance: NickArcadeExtension
    }

    lateinit var service: HypixelService
    lateinit var hypixelPlayerInfoHelper: HypixelPlayerInfoHelper

    override fun initialize() {
        instance = this
        commandManager = NickArcadeCommandHelper().init()
        registerDisplayPacketHandler()

        initMainConfig()
        initHypixelServices() ?: run {
            throw Exception("Unable to initialize plugin! Reason: Unable to initialize Hypixel services")
        }

        SchematicManager.hasSchematicByName(SchematicName.LOBBY).takeIf { it }
            ?: throw Exception("Unable to find lobby schematic.")

        commandManager.registerCommands()

        registerJoinEvents()
        registerPlayerEvents()
        registerPlayerDataEvents()
        registerLeaveEvents()
        registerGameEvents()

        loadProfilesManager()

        //registerFireballEvents()
    }

    private fun loadProfilesManager() {
        val directory = File(dataFolder, "profiles").also { it.mkdirs() }
        ProfilesManager.loadProfiles(directory)
        logger.info("Loaded ${ProfilesManager.profiles.count()} profiles from dump!")
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

    override fun terminate() {

    }
}

fun registerFireballEvents() {

    /*event<EntityExplodeEvent> {
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
    }*/
}

private fun NickArcadeCommandHelper.registerCommands() {
    annotationParser.parse(TestCommands)
    annotationParser.parse(ImpersonateCommands)
    annotationParser.parse(PartyCommands)
    annotationParser.parse(MiscCommands)
    annotationParser.parse(RankCommands)
    annotationParser.parse(ChatCommands)
    ChatChannelsManager.registerChatChannelCommands(this)
    RankCommands.registerOverrideRanksCommands(this)
}
