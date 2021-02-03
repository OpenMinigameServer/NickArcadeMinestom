package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.game.GameState
import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import io.github.nickacpt.nickarcade.schematics.manager.SchematicName
import io.github.nickacpt.nickarcade.schematics.manager.clipboard
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import io.github.nickacpt.nickarcade.utils.interop.launch
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.GameMode
import net.minestom.server.event.PlayerEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.item.Material


val lobbyInstance by lazy {
    SchematicManager.getInstanceForSchematic(SchematicName.LOBBY)
        ?: throw Exception("Unable to find lobby schematic.")
}

const val playerPlacedTag = "player_placed"
fun registerPlayerEvents() {

    // Create the instance
    lobbyInstance

    //#PlayerInit
    event<PlayerLoginEvent>(forceBlocking = true) {
        val player = player

        player.isAllowFlying = true
        player.isFlying = true
        player.gameMode = GameMode.CREATIVE

        setSpawningInstance(lobbyInstance)

        player.respawnPoint = MinestomAdapter.toPosition(lobbyInstance.clipboard!!.origin.toVector3())
    }

    event<PlayerSpawnEvent> {
        if (!isFirstSpawn) return@event

        sendPlayerDataActionBar()

        val playerData = async {
            player.getArcadeSender()
        }

        callEvent(PlayerDataJoinEvent(playerData))
        callEvent(PlayerDataReloadEvent(playerData))
    }

    event<PlayerBlockPlaceEvent>(forceBlocking = true, ignoreCancelled = true) {
        if (player.instance?.getBlockStateId(blockPosition)?.takeIf { it != Material.AIR.id } != null) {
            isCancelled = true
            return@event
        }
        blockData = DataImpl().also {
            it.set(playerPlacedTag, true)
        }
    }
    event<PlayerBlockBreakEvent>(forceBlocking = true) {
        val data = player.instance?.getBlockData(this.blockPosition)
        if (data == null || !data.hasKey(playerPlacedTag)) {
            isCancelled = true
            val arcadeSender = this.player.getArcadeSender()
            if (arcadeSender.getCurrentGame()?.state == GameState.IN_GAME) {
                player.asAudience.sendMessage(
                    Component.text(
                        "You can only break blocks placed by players!",
                        NamedTextColor.RED
                    )
                )
            }
        }
    }

}

private fun PlayerEvent.sendPlayerDataActionBar() {
    pluginInstance.launch {
        val audience = player.asAudience
        while (!PlayerDataManager.isPlayerDataLoaded(player.uniqueId)) {
            audience.sendActionBar(
                Component.text(
                    "Fetching player data from Hypixel, please wait!",
                    NamedTextColor.RED,
                    TextDecoration.BOLD
                )
            )
            delay(5.ticks)
        }
        audience.sendActionBar(
            Component.text(
                "Player data fetched from Hypixel! Have a nice stay.",
                NamedTextColor.GREEN
            )
        )
    }
}
