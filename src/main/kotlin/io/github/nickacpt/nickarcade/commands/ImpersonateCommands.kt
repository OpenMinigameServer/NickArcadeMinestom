package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationData
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

object ImpersonateCommands {
    private val profileService = ProfileApi.getProfileService()

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("impersonate <player>")
    fun impersonatePlayer(sender: ArcadePlayer, @Argument("player") nameOrUUID: String) =
        command(sender, HypixelPackageRank.ADMIN) {
            val senderAudience = sender.audience
            val result = profileService.findById(nameOrUUID)
            if (result?.isError == true) {
                senderAudience.sendMessage(text("Unable to find a player with that name/id", NamedTextColor.RED))
                return@command
            }

            val uniqueId = result?.uniqueId
            val name = result?.name
            if (uniqueId != null && name != null) {
                performPlayerReLogin(sender) {
                    ImpersonationManager.impersonate(sender.uuid, ImpersonationData(name, uniqueId))
                    senderAudience.sendMessage(text("You are now impersonating user $name.", NamedTextColor.GREEN))
                }
            }
        }

    @CommandMethod("removeimpersonation")
    fun removeImpersonation(sender: ArcadePlayer) =
        command(sender) {
            val senderAudience = sender.audience
            performPlayerReLogin(sender) {
                ImpersonationManager.removeImpersonation(sender.uuid)
                senderAudience.sendMessage(
                    text(
                        "You are no longer impersonating a user.",
                        NamedTextColor.GREEN
                    )
                )
            }
        }


    private suspend fun performPlayerReLogin(sender: ArcadePlayer, code: suspend () -> Unit) {
        logoutPlayer(sender)
        code()
    }

    private fun logoutPlayer(sender: ArcadePlayer) {
        sender.player?.kick("Please reconnect to finish applying impersonation!")
    }

}