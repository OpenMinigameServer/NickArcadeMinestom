package io.github.nickacpt.nickarcade.party.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.interop.async
import io.github.nickacpt.nickarcade.utils.pluginInstance
import io.github.nickacpt.nickarcade.utils.separator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import kotlin.time.minutes

val partyExpiryTime = 1.minutes

data class Party(
    var leader: PlayerData,
    val members: MutableList<PlayerData> = mutableListOf(),
    val pendingInvites: MutableList<PlayerData> = mutableListOf()
) {
    private val membersCount = members.count { it != leader }

    fun hasPendingInvite(player: PlayerData): Boolean {
        return pendingInvites.contains(player)
    }

    fun switchOwner(newOwner: PlayerData) {
        addMember(leader)
        leader = newOwner
        addMember(newOwner)
    }

    fun invitePlayer(sender: PlayerData, target: PlayerData) {
        if (hasPendingInvite(target)) {
            sender.audience.sendMessage(separator {
                append(text(target.getChatName(true)))
                append(text(" has already been invited to the party.", NamedTextColor.RED))
            })
            return
        }
        if (!target.isOnline) {
            sender.audience.sendMessage(separator {
                append(text("You cannot invite that player since they're not online.", NamedTextColor.RED))
            })
            return
        }

        pluginInstance.async(scheduleInviteExpirationActions(sender, target))

        audience.sendMessage(separator(NamedTextColor.BLUE) {
            append(text(sender.getChatName(true)))
            append(text(" invited ", NamedTextColor.YELLOW))
            append(text(target.getChatName(true)))
            append(text(" to the party! They have ", NamedTextColor.YELLOW))
            append(text(partyExpiryTime.inSeconds.toInt(), NamedTextColor.RED))
            append(text(" seconds to accept.", NamedTextColor.YELLOW))
        })

        target.audience.sendMessage(separator(NamedTextColor.BLUE) {
            append(text(sender.getChatName(true)))
            append(text(" invited you to their party!", NamedTextColor.YELLOW))
            append(newline())
            val command = "/party accept ${sender.actualDisplayName}"
            append(text {
                it.append(text("You have ", NamedTextColor.YELLOW))
                it.append(text(partyExpiryTime.inSeconds.toInt(), NamedTextColor.RED))
                it.append(text(" seconds to accept.", NamedTextColor.YELLOW))
            }.clickEvent(ClickEvent.runCommand(command))).hoverEvent(text("Click to run $command"))
        })
    }

    private fun scheduleInviteExpirationActions(
        sender: PlayerData,
        target: PlayerData
    ): suspend CoroutineScope.() -> Unit = scope@{
        pendingInvites.add(target)
        delay(partyExpiryTime)
        if (!hasPendingInvite(target)) {
            return@scope
        }
        pendingInvites.remove(target)
        target.audience.sendMessage(separator {
            append(text("The party invite from ", NamedTextColor.YELLOW))
            append(text(sender.getChatName(true)))
            append(text(" has expired.", NamedTextColor.YELLOW))
        })

        audience.sendMessage(separator {
            append(text("The party invite to ", NamedTextColor.YELLOW))
            append(text(target.getChatName(true)))
            append(text(" has expired.", NamedTextColor.YELLOW))
        })
    }

    fun addMember(member: PlayerData, broadcast: Boolean = false) {
        PartyManager.addMember(this, member)

        if (broadcast) {
            audience.sendMessage(separator {
                append(text(member.getChatName(true)))
                append(text(" joined the party.", NamedTextColor.YELLOW))
            })
        }
    }

    fun removeMember(member: PlayerData, broadcast: Boolean = false, isKick: Boolean = false) {
        PartyManager.removeMember(this, member)

        if (broadcast) {
            audience.sendMessage(separator {
                append(text(member.getChatName(true)))
                if (isKick) {
                    append(text(" has been kicked from the party.", NamedTextColor.YELLOW))
                } else {
                    append(text(" left the party.", NamedTextColor.YELLOW))
                }
            })
        }

    }

    private fun TextComponent.Builder.appendPlayerData(it: PlayerData) {
        append(text(it.getChatName(true)))
        append(Component.space())
        append(text('●', if (it.isOnline) NamedTextColor.GREEN else NamedTextColor.RED))
        append(Component.space())
    }

    fun disband() {
        val list = members + leader
        list.forEach {
            removeMember(it)
        }
    }

    fun restorePlayer(player: PlayerData) {
        if (player == leader) {
            leader = player
        } else if (members.removeIf { it == player }) {
            members.add(player)
        }
    }

    fun isLeader(sender: PlayerData): Boolean {
        return leader == sender
    }

    val listMessage: Component
        get() {
            return separator {
                append(text("Party members (${totalMemberCount})", NamedTextColor.GOLD)); append(newline())
                append(newline())
                append(text("Party Leader: ", NamedTextColor.YELLOW))
                appendPlayerData(leader); append(newline())
                if (membersCount > 0) {
                    append(text("Party Members: ", NamedTextColor.YELLOW));
                    members.forEach {
                        appendPlayerData(it)
                    }
                }
            }
        }
    private val totalMemberCount: Int
        get() = members.size

    val id: UUID = UUID.randomUUID()
    var settings: PartySettings = PartySettings(this)

    @JsonIgnore
    val audience = PartyAudience(this)
}