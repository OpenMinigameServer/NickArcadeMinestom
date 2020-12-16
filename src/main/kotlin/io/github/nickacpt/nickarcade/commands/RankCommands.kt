package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Greedy
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.nickarcade.data.PlayerData
import io.github.nickacpt.nickarcade.data.PlayerDataManager
import io.github.nickacpt.nickarcade.data.PlayerOverrides
import io.github.nickacpt.nickarcade.utils.asAudience
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.div
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import org.bukkit.command.CommandSender
import org.checkerframework.checker.nullness.qual.NonNull
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.exists

object RankCommands {

    @CommandMethod("ranks|rank get <player>")
    fun getPlayerInfo(
        sender: CommandSender,
        @Argument("player") player: PlayerData
    ) = command(sender, HypixelPackageRank.ADMIN) {
        sender.asAudience.sendMessage {
            text {
                it.append(text(player.getChatName()))
                it.append(text(" is player's display on this server.", GREEN))
            }
        }
    }

    @CommandMethod("ranks|rank list <rank>")
    fun listPlayersRanked(
        sender: CommandSender,
        @Argument("rank") rank: HypixelPackageRank
    ) = command(sender, HypixelPackageRank.ADMIN) {
        val players =
            PlayerDataManager.playerDataCollection.find(
                and(
                    (PlayerData::overrides / PlayerOverrides::rankOverride).exists(),
                    PlayerData::overrides / PlayerOverrides::rankOverride eq rank
                )
            ).toList()

        sender.asAudience.sendMessage {
            text {
                it.append(text("Found ${players.size} player(s) with rank $rank:", GREEN))
                players.forEach { p ->
                    it.append(newline())
                    it.append(text(p.getChatName()).hoverEvent(p.computeHoverEventComponent()))
                }
            }
        }
    }

    @CommandMethod("ranks|rank set <player> <rank>")
    fun setPlayerRank(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Argument("rank") rank: HypixelPackageRank
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.rankOverride = rank
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully set ${playerData.displayName}'s rank to ${rank.name}",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank setplus <player> <color>")
    fun setPlayerRankPlus(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Argument("color") color: MinecraftChatColor
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.rankPlusColorOverride = color
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully set ${playerData.displayName}'s plus color to ${color.name}",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank resetplus <player>")
    fun setPlayerRankPlus(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.rankPlusColorOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully reset ${playerData.displayName}'s plus color",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank setmonthlyrank <player> <color>")
    fun setPlayerMonthlyRankColor(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Argument("color") color: MinecraftChatColor
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.monthlyRankColorOverride = color
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully set ${playerData.displayName}'s monthly rank color to ${color.name}",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank resetmonthlyrank <player>")
    fun resetPlayerMonthlyRankColor(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.monthlyRankColorOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully reset ${playerData.displayName}'s monthly rank color",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank reset|remove <player>")
    fun resetPlayerRank(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.rankOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully reset ${playerData.displayName}'s rank.",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank setprefix <player> <prefix>")
    fun setPlayerPrefix(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData,
        @Greedy @Argument("prefix") prefix: String
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.prefixOverride = "${prefix.trim().replace('&', '§')} "
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully set ${playerData.displayName}'s prefix.",
                GREEN
            ), playerData
        )
    }

    @CommandMethod("ranks|rank resetprefix|removeprefix <player>")
    fun resetPlayerPrefix(
        sender: CommandSender,
        @Argument("player") playerData: PlayerData
    ): Unit = command(sender, HypixelPackageRank.ADMIN) {
        playerData.overrides.prefixOverride = null
        PlayerDataManager.savePlayerData(playerData)
        sendSuccessMessage(
            sender, text(
                "Successfully reset ${playerData.displayName}'s prefix.",
                GREEN
            ), playerData
        )
    }

    private fun sendSuccessMessage(
        sender: CommandSender,
        message: @NonNull TextComponent,
        playerData: PlayerData
    ) {
        sender.asAudience.sendMessage(
            text {
                it.append(
                    message
                )
                it.append(newline())
                it.append(computeDisplayNameMessage(playerData))
            }

        )
    }

    private fun computeDisplayNameMessage(playerData: PlayerData) = text(
        "Their display name is now ",
        GREEN
    ).append(text(playerData.getChatName()))
}