package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationData
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.utils.asAudience
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.interop.uniqueId
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player

object ImpersonateCommands {
    private val profileService = ProfileApi.getProfileService()

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("impersonate <player>")
    fun impersonatePlayer(sender: Player, @Argument("player") nameOrUUID: String) =
        command(sender, HypixelPackageRank.ADMIN) {
            val senderAudience = sender.asAudience
            val result = profileService.findById(nameOrUUID)
            if (result?.isError == true) {
                senderAudience.sendMessage(text("Unable to find a player with that name/id", NamedTextColor.RED))
                return@command
            }

            val uniqueId = result?.uniqueId
            val name = result?.name
            if (uniqueId != null && name != null) {
                performPlayerReLogin(sender) {
                    ImpersonationManager.impersonate(sender.uniqueId, ImpersonationData(name, uniqueId))
                    senderAudience.sendMessage(text("You are now impersonating user $name.", NamedTextColor.GREEN))
                }
            }
        }

    @CommandMethod("removeimpersonation")
    fun removeImpersonation(sender: Player) =
        command(sender) {
            val senderAudience = sender.asAudience
            performPlayerReLogin(sender) {
                ImpersonationManager.removeImpersonation(sender.uniqueId)
                senderAudience.sendMessage(
                    text(
                        "You are no longer impersonating a user.",
                        NamedTextColor.GREEN
                    )
                )
            }
        }


    private suspend fun performPlayerReLogin(sender: Player, code: suspend () -> Unit) {
        logoutPlayer(sender)
        code()
    }

    private fun logoutPlayer(sender: Player) {
        sender.kick("Please reconnect to finish applying impersonation!")
    }

}