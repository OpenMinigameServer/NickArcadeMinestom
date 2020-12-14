package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Greedy
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.data.PlayerDataManager
import io.github.nickacpt.nickarcade.utils.asAudience
import io.github.nickacpt.nickarcade.utils.command
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

object RankCommands {
    @CommandPermission
    @CommandMethod("ranks set <player> <rank>")
    fun setPlayerRank(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Argument("rank") rank: HypixelPackageRank
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.rankOverride = rank
        PlayerDataManager.savePlayerData(playerData)
        sender.asAudience.sendMessage(
            text(
                "Successfully set ${playerData.displayName}'s rank to ${rank.name}",
                NamedTextColor.GREEN
            )
        )
    }

    @CommandMethod("ranks reset <player>")
    fun resetPlayerRank(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.rankOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sender.asAudience.sendMessage(
            text(
                "Successfully reset ${playerData.displayName}'s rank.",
                NamedTextColor.GREEN
            )
        )
    }

    @CommandMethod("ranks setprefix <player> <prefix>")
    fun setPlayerPrefix(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Greedy @Argument("prefix") prefix: String
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.prefixOverride = "${prefix.trim().replace('&', '§')} "
        PlayerDataManager.savePlayerData(playerData)
        sender.asAudience.sendMessage(
            text(
                "Successfully set ${playerData.displayName}'s prefix.",
                NamedTextColor.GREEN
            )
        )
    }

    @CommandMethod("ranks resetprefix <player>")
    fun resetPlayerPrefix(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.prefixOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sender.asAudience.sendMessage(
            text(
                "Successfully set ${playerData.displayName}'s prefix.",
                NamedTextColor.GREEN
            )
        )
    }
}