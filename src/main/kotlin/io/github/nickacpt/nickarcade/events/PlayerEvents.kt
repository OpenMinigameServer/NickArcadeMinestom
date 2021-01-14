package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.nickarcade.schematics.manager.SchematicManager
import io.github.nickacpt.nickarcade.schematics.manager.SchematicName
import io.github.nickacpt.nickarcade.schematics.manager.clipboard
import io.github.nickacpt.nickarcade.utils.asAudience
import io.github.nickacpt.nickarcade.utils.event
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerLoginEvent
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
            player.asAudience.sendMessage(
                Component.text(
                    "You can only break blocks placed by players!",
                    NamedTextColor.RED
                )
            )
        }
    }

}