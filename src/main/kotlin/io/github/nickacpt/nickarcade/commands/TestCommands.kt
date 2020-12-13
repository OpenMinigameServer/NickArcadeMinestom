package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.nickarcade.utils.command
import kotlinx.coroutines.delay
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

object TestCommands {

    @CommandMethod("testsuspendingfunc")
    fun testSuspendingFunc(sender: Player) = command(sender) {
        sender.sendMessage(ChatColor.GREEN.toString() + "hey")
        delay(5000)
        sender.sendMessage(ChatColor.GREEN.toString() + "hey 5s later")
    }
}