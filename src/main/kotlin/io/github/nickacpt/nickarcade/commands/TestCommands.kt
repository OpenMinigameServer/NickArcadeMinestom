package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Greedy
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.events.playerPlacedTag
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.MiniGameType
import io.github.nickacpt.nickarcade.game.definition.position.GamePosition
import io.github.nickacpt.nickarcade.testflows.TestFlow
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.RanksHelper.randomPlayerOverrides
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.debugsubjects.DebugSubjectPlayer
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import java.util.*
import kotlin.math.floor

object TestCommands {


    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("randomprofile")
    fun testRandomProfile(sender: ArcadePlayer) = command(sender, HypixelPackageRank.ADMIN) {
        val profile = ProfilesManager.profiles.random()
        sender.displayOverrides.overrides =
            randomPlayerOverrides()

        sender.player?.setDisplayProfile(profile, true)
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("randomprofile remove")
    fun removeRandomProfile(sender: ArcadePlayer) = command(sender, HypixelPackageRank.ADMIN) {
        sender.displayOverrides.overrides = null

        sender.player?.setDisplayProfile(null, true)
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debug load <target>")
    fun debugLoadProfile(sender: ArcadePlayer, @Argument("target") target: ArcadePlayer) =
        command(sender, HypixelPackageRank.ADMIN) {
            sender.audience.sendMessage(text {
                it.append(text(target.getChatName(true)))
                it.append(text(" is now being a dummy subject.", NamedTextColor.GREEN))
            })
            target.forwardTarget = sender
        }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debug unload <target>")
    fun debugUnloadProfile(sender: ArcadePlayer, @Argument("target") target: ArcadePlayer) =
        command(sender, HypixelPackageRank.ADMIN) {
            target.forwardTarget = null
            PlayerDataManager.removePlayerData(target.uuid)
            sender.audience.sendMessage(text {
                it.append(text(target.getChatName(true)))
                it.append(text(" is no longer a dummy subject.", NamedTextColor.GREEN))
            })
        }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debug run <target> <command>")
    fun debugRunCommand(
        sender: ArcadePlayer,
        @Argument("target") target: ArcadePlayer,
        @Argument("command") @Greedy command: String
    ) =
        command(sender, HypixelPackageRank.ADMIN) {
            if (target.forwardTarget == null) return@command

            pluginInstance.commandManager.manager.executeCommand(
                DebugSubjectPlayer(
                    target.player!!,
                    target
                ).getArcadeSender(), command
            )
        }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("debugcreateparty")
    fun createDebugParty(sender: ArcadePlayer) = command(sender) {
        sender.getOrCreateParty()
    }

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("scaffold")
    fun scaffold(sender: ArcadePlayer) = command(sender) {
        event<PlayerMoveEvent> {
            val instance = this.player.instance ?: return@event
            val blocc = BlockPosition(floor(newPosition.x), floor(newPosition.y) - 1f, floor(newPosition.z))
            instance.setBlock(blocc, Block.STONE)
            instance.setBlockData(blocc, DataImpl().also { it.set(playerPlacedTag, true) })
        }
    }

    @CommandMethod("testflow <flow>")
    fun runTestFlow(sender: ArcadePlayer, @Argument("flow") flow: TestFlow) = command(sender) {
        sender.audience.sendMessage(text("Executing test flow ${flow.name.toLowerCase()}..", NamedTextColor.GRAY))
        flow.implementation.execute(sender)
    }


    @CommandMethod("debugjoingame <type>")
    fun joinDebugGame(sender: ArcadePlayer, @Argument("type") type: MiniGameType) = command(sender) {

        sender.audience.sendMessage(text("Creating a game instance for you..", NamedTextColor.GRAY))

        val game = MiniGameManager.createGame(
            type,
            ArenaDefinition(
                "Glacier",
                2,
                4,
                "glacier",
                54f,
                GamePosition(0.0, 60.0, 0.0)
            )
        )
        game?.addPlayer(sender)
    }
}