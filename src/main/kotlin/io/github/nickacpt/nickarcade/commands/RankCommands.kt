package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.PlayerDataManager
import io.github.nickacpt.nickarcade.data.getPlayerData
import io.github.nickacpt.nickarcade.utils.command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object RankCommands {
    @CommandPermission
    @CommandMethod("setrank <player> <rank>")
    fun setPlayerRank(
        sender: CommandSender,
        @Argument("player") target: Player,
        @Argument("rank") rank: HypixelPackageRank
    ): Unit = command(sender) {
        val playerData = target.getPlayerData()
        playerData.rankOverride = rank
        PlayerDataManager.savePlayerData(playerData)
        sender.sendMessage(ChatColor.GREEN.toString() + "Successfully set ${target.name}'s rank to ${rank.name}")
    }

    @CommandMethod("resetrank <player>")
    fun resetPlayerRank(
        sender: CommandSender,
        @Argument("player") target: Player
    ): Unit = command(sender) {
        val playerData = target.getPlayerData()
        playerData.rankOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sender.sendMessage(ChatColor.GREEN.toString() + "Successfully reset ${target.name}'s rank.")
    }
}