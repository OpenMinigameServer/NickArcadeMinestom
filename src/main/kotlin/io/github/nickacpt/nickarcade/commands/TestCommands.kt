package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Greedy
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.events.playerPlacedTag
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.MiniGameType
import io.github.nickacpt.nickarcade.game.definition.position.GamePosition
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.debugsubjects.DebugSubjectPlayer
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import java.util.*
import kotlin.math.floor
import kotlin.random.Random

object TestCommands {


    val ranksRange = EnumSet.range(HypixelPackageRank.NORMAL, HypixelPackageRank.MVP_PLUS)

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("randomprofile")
    fun testRandomProfile(sender: Player) = command(sender, HypixelPackageRank.ADMIN) {
        val profile = ProfilesManager.profiles.random()
        val playerData = sender.getPlayerData()
        playerData.displayOverrides.overrides =
            PlayerOverrides(ranksRange.random(), networkLevel = Random.nextInt(1, 50).toLong())

        sender.setDisplayProfile(profile, true)
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("randomprofile remove")
    fun removeRandomProfile(sender: Player) = command(sender, HypixelPackageRank.ADMIN) {
        val playerData = sender.getPlayerData()
        playerData.displayOverrides.overrides = null

        sender.setDisplayProfile(null, true)
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debug load <target>")
    fun debugLoadProfile(sender: Player, @Argument("target") target: PlayerData) =
        command(sender, HypixelPackageRank.ADMIN) {
            sender.asAudience.sendMessage(text {
                it.append(text(target.getChatName(true)))
                it.append(text(" is now being a dummy subject.", NamedTextColor.GREEN))
            })
            target.forwardTarget = sender.getPlayerData()
        }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debug unload <target>")
    fun debugUnloadProfile(sender: Player, @Argument("target") target: PlayerData) =
        command(sender, HypixelPackageRank.ADMIN) {
            target.forwardTarget = null
            PlayerDataManager.removePlayerData(target.uuid)
            sender.asAudience.sendMessage(text {
                it.append(text(target.getChatName(true)))
                it.append(text(" is no longer a dummy subject.", NamedTextColor.GREEN))
            })
        }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debug run <target> <command>")
    fun debugRunCommand(
        sender: Player,
        @Argument("target") target: PlayerData,
        @Argument("command") @Greedy command: String
    ) =
        command(sender, HypixelPackageRank.ADMIN) {
            if (target.forwardTarget == null) return@command

            pluginInstance.commandManager.manager.executeCommand(DebugSubjectPlayer(target.player!!, target), command)
        }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debugcreateparty")
    fun createDebugParty(sender: Player) = command(sender) {
        sender.getPlayerData().getOrCreateParty()
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("scaffold")
    fun scaffold(sender: Player) = command(sender) {
        event<PlayerMoveEvent> {
            val instance = this.player.instance ?: return@event
            val blocc = BlockPosition(floor(newPosition.x), floor(newPosition.y) - 1f, floor(newPosition.z))
            instance.setBlock(blocc, Block.STONE)
            instance.setBlockData(blocc, DataImpl().also { it.set(playerPlacedTag, true) })
        }
    }

    @CommandMethod("debugjoingame <type>")
    fun joinDebugGame(sender: Player, @Argument("type") type: MiniGameType) = command(sender) {
        val player = sender.getPlayerData()

        sender.asAudience.sendMessage(text("Creating a game instance for you..", NamedTextColor.GRAY))

        val game = MiniGameManager.createGame(
            type,
            ArenaDefinition(
                "Glacier",
                1,
                2,
                "glacier",
                54f,
                GamePosition(0f, 60f, 0f)
            )
        )
        game?.addPlayer(player)
    }
}