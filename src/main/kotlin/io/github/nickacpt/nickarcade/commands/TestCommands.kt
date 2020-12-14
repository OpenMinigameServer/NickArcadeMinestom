package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.utils.actualPlayerProfile
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import kotlinx.coroutines.delay
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import kotlin.time.seconds

object TestCommands {

    @CommandMethod("testsuspendingfunc")
    fun testSuspendingFunc(sender: Player) = command(sender) {
        sender.sendMessage(ChatColor.GREEN.toString() + "hey")
        delay(5000)
        sender.sendMessage(ChatColor.GREEN.toString() + "hey 5s later")
    }

    @CommandMethod("randomprofile")
    fun testRandomProfile(sender: Player) = command(sender, HypixelPackageRank.ADMIN) {
        while (sender.isOnline) {
            val asPlayerProfile = ProfilesManager.profiles.random().asPlayerProfile(sender.uniqueId)
            sender.actualPlayerProfile = asPlayerProfile
            sender.setPlayerListName(null)
            delay(2.5.seconds)
        }
    }
}