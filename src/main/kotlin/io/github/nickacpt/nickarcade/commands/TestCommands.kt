package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import kotlinx.coroutines.delay
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.*
import kotlin.random.Random

object TestCommands {


    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("testsuspendingfunc")
    fun testSuspendingFunc(sender: Player) = command(sender) {
        sender.sendMessage(ChatColor.GREEN.toString() + "hey")
        delay(5000)
        sender.sendMessage(ChatColor.GREEN.toString() + "hey 5s later")
    }

    val ranksRange = EnumSet.range(HypixelPackageRank.NORMAL, HypixelPackageRank.MVP_PLUS)

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("randomprofile")
    fun testRandomProfile(sender: Player) = command(sender, HypixelPackageRank.ADMIN) {
        val profile = ProfilesManager.profiles.random()
        val asPlayerProfile = profile.asPlayerProfile(sender.uniqueId)
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
}