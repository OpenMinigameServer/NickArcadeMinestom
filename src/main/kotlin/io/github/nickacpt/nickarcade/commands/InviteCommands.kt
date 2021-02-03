package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.hypixelapi.utis.profile.Profile
import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.chat.ChatMessageOrigin
import io.github.nickacpt.nickarcade.chat.impl.StaffChatChannel
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.data.player.NickArcadeWatcherSender
import io.github.nickacpt.nickarcade.invite.InviteManager
import io.github.nickacpt.nickarcade.invite.InviteManager.getRemainingInvites
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.cooldown
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor.*
import java.util.*
import kotlin.time.seconds

object InviteCommands {

    @CommandMethod("invite remove <name>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun removePlayerInviteByName(sender: ArcadeSender, @Argument("name") name: String) =
        command(sender, HypixelPackageRank.ADMIN) {
            val findResult = findPlayerByName(sender, name) ?: return@command
            val resultingPlayer = findResult.first
            val invitedUUID = findResult.second

            InviteManager.removePlayerInvite(invitedUUID)
            sender.audience.sendMessage(text("Invite for ${resultingPlayer.name!!} was removed successfully.", GREEN))
            StaffChatChannel.sendMessageInternal(
                NickArcadeWatcherSender, "${MinecraftChatColor.AQUA}[${
                    sender.getChatName(
                        actualData = true,
                        colourPrefixOnly = true
                    )
                }${MinecraftChatColor.AQUA}] removed invite for [$name]", ChatMessageOrigin.SHORTCUT_COMMAND
            )
        }

    @CommandMethod("invite <name>")
    fun invitePlayerByName(sender: ArcadeSender, @Argument("name") name: String) = command(sender) {
        if (!InviteManager.canInvitePlayers(sender)) {
            sender.audience.sendMessage(text("You have exceeded your player invite limit!", RED))
            return@command
        }

        val findResult = findPlayerByName(sender, name) ?: return@command
        val resultingPlayer = findResult.first
        val invitedUUID = findResult.second

        if (InviteManager.hasPlayerReceivedInvite(invitedUUID)) {
            sender.audience.sendMessage(text("That player has already been invited to this server!", RED))
            return@command
        }

        val requiresConfirmation = !sender.hasAtLeastRank(HypixelPackageRank.ADMIN, true)
        if (requiresConfirmation && sender is ArcadePlayer) {
            val isConfirmation = !sender.player!!.cooldown("player-invite", 10.seconds)
            if (!isConfirmation) {
                sender.audience.sendMessage(text("Please type the command again to confirm this action!", RED))
                val remainingInvites = getRemainingInvites(sender) - 1
                sender.audience.sendMessage(text {
                    it.append(text("Once the invite has been sent, ", RED))
                    if (remainingInvites == 0L) {
                        it.append(text("you will no longer be able to invite more players.", RED))
                    } else {
                        it.append(
                            text(
                                "you will able to invite $remainingInvites more player${if (remainingInvites != 1L) "s" else ""}.",
                                RED
                            )
                        )
                    }
                })
                return@command
            }
            sender.cooldowns.remove("player-invite")
        }
        InviteManager.addPlayerInvite(invitedUUID, sender.uuid)
        StaffChatChannel.sendMessageInternal(
            NickArcadeWatcherSender, "${MinecraftChatColor.AQUA}[${
                sender.getChatName(
                    actualData = true,
                    colourPrefixOnly = true
                )
            }${MinecraftChatColor.AQUA}] invited [$name]", ChatMessageOrigin.SHORTCUT_COMMAND
        )
        sender.audience.sendMessage(
            text {
                it.append(text("You have successfully invited player ", GREEN))
                it.append(text(resultingPlayer.name!!, GOLD))
                it.append(text(" to the server!", GREEN))
                if (requiresConfirmation) {
                    it.append(newline())
                    it.append(text("This action can only be reverted by an administrator.", YELLOW))
                }
            }
        )
    }

    private suspend fun findPlayerByName(sender: ArcadeSender, name: String): Pair<Profile, UUID>? {
        sender.audience.sendMessage(text("Processing your request..", GRAY))

        val resultingPlayer = ProfileApi.getProfileByName(name)
        val invitedUUID = resultingPlayer?.uuid
        if (invitedUUID == null) {
            sender.audience.sendMessage(text("That player does not exist!", RED))
            return null
        }
        return resultingPlayer to invitedUUID
    }

}