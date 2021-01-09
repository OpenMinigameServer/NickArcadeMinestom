package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Greedy
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.MiniGameType
import io.github.nickacpt.nickarcade.game.definition.position.GamePosition
import io.github.nickacpt.nickarcade.utils.asAudience
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.debugsubjects.DebugSubjectPlayer
import io.github.nickacpt.nickarcade.utils.pluginInstance
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import java.util.*
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

    @CommandMethod("debugjoingame <type>")
    fun joinDebugGame(sender: Player, @Argument("type") type: MiniGameType) = command(sender) {
        val player = sender.getPlayerData()

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
        game.addPlayer(player)
    }
}