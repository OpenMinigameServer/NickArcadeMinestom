package io.github.nickacpt.nickarcade.display

import kotlinx.coroutines.runBlocking
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.PlayerInfoPacket
import net.minestom.server.network.packet.server.play.SpawnPlayerPacket
import java.util.*

object DisplayPacketHandler {

    fun registerDisplayPacketHandler() {
        MinecraftServer.getConnectionManager().onPacketSend { players, _, packet ->
            if (packet is PlayerInfoPacket) {
                val extraActions = mutableListOf<PlayerInfoPacket.PlayerInfo>()
                packet.playerInfos.forEach { it.modifyUuid(packet.action, extraActions, players) }
                packet.playerInfos.addAll(extraActions)
            } else if (packet is SpawnPlayerPacket) {
                packet.playerUuid = playerIds[packet.playerUuid] ?: packet.playerUuid
            }
        }
    }


    //Maps original IDs to fake IDs
    private val playerIds = mutableMapOf<UUID, UUID>()
    private fun PlayerInfoPacket.PlayerInfo.modifyUuid(action: PlayerInfoPacket.Action, extraActions: MutableList<PlayerInfoPacket.PlayerInfo>,
        players: MutableCollection<Player>
    ) {
        val player = MinecraftServer.getConnectionManager().getPlayer(uuid) ?: return
        val newId: UUID =
            when (action) {
                PlayerInfoPacket.Action.REMOVE_PLAYER -> {
                    extraActions.add(PlayerInfoPacket.RemovePlayer(playerIds[uuid] ?: uuid))
                    uuid
                }
                PlayerInfoPacket.Action.ADD_PLAYER -> {
                    runBlocking {
                        PlayerUuidProviderManager.getPlayerUuid(player)
                    }
                }
                else -> uuid
            }
        if (action == PlayerInfoPacket.Action.ADD_PLAYER) {
            playerIds[uuid] = newId
        } else if (action == PlayerInfoPacket.Action.REMOVE_PLAYER) {
            playerIds.remove(uuid)
        }
        if (players.none { it.uuid == uuid })
            uuid = newId
    }

}