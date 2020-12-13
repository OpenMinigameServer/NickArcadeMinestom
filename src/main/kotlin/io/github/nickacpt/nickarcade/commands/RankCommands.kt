package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Greedy
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.data.PlayerDataManager
import io.github.nickacpt.nickarcade.utils.command
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RankCommands {
    @CommandPermission
    @CommandMethod("ranks set <player> <rank>")
    fun setPlayerRank(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Argument("rank") rank: HypixelPackageRank
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.rankOverride = rank
        PlayerDataManager.savePlayerData(playerData)
        sender.sendMessage(ChatColor.GREEN.toString() + "Successfully set ${playerData.displayName}'s rank to ${rank.name}")
    }

    @CommandMethod("ranks reset <player>")
    fun resetPlayerRank(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.rankOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sender.sendMessage(ChatColor.GREEN.toString() + "Successfully reset ${playerData.displayName}'s rank.")
    }

    @CommandMethod("ranks setprefix <player> <prefix>")
    fun setPlayerPrefix(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Greedy @Argument("prefix") prefix: String
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.prefixOverride = "${prefix.trim().replace('&', '§')} "
        PlayerDataManager.savePlayerData(playerData)
        sender.sendMessage(ChatColor.GREEN.toString() + "Successfully set ${playerData.displayName}'s prefix.")
    }

    @CommandMethod("ranks resetprefix <player>")
    fun resetPlayerPrefix(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.prefixOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sender.sendMessage(ChatColor.GREEN.toString() + "Successfully set ${playerData.displayName}'s prefix.")
    }
}