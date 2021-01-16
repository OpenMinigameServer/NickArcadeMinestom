package io.github.nickacpt.nickarcade.utils.debugsubjects

import com.google.gson.JsonParser
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.utils.asAudience
import net.kyori.adventure.platform.minestom.MinestomComponentSerializer
import net.kyori.adventure.text.Component
import net.minestom.server.chat.JsonMessage
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.player.PlayerConnection
import java.net.InetSocketAddress
import java.net.SocketAddress

class DebugSubjectConnection : PlayerConnection() {
    override fun sendPacket(serverPacket: ServerPacket) {
    }

    override fun getRemoteAddress(): SocketAddress {
        return InetSocketAddress(0)
    }

    override fun disconnect() {
    }
}

class DebugSubjectPlayer(val owner: Player, val target: ArcadePlayer) :
    Player(target.uuid, target.actualDisplayName, DebugSubjectConnection()) {
    private val prefix = "[${target.actualDisplayName}] "
    override fun sendMessage(message: String) {
        owner.sendMessage(prefix + message)
    }

    override fun sendMessage(message: JsonMessage) {
        val deserialize = MinestomComponentSerializer.get().deserialize(message)
        owner.asAudience.sendMessage(Component.text(prefix).append(deserialize))
    }

    override fun sendJsonMessage(json: String) {
        val deserialize = JsonMessage.RawJsonMessage(JsonParser.parseString(json).asJsonObject)
        sendMessage(deserialize)
    }
}