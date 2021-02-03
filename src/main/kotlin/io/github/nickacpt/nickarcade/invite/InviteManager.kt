package io.github.nickacpt.nickarcade.invite

import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.utils.pluginInstance
import kotlinx.datetime.Clock
import org.litote.kmongo.eq
import java.util.*

object InviteManager {

    private val inviteCollection by lazy {
        pluginInstance.database.getCollection<PlayerInvite>("invites")
    }

    suspend fun getPlayerInvite(invited: UUID): PlayerInvite? {
        return inviteCollection.findOne(PlayerInvite::invited eq invited)
    }

    suspend fun getPlayerInvitesByPlayer(inviter: UUID): List<PlayerInvite> {
        return inviteCollection.find(PlayerInvite::inviter eq inviter).toList()
    }

    private suspend fun countPlayerInvitesByPlayer(inviter: UUID): Long {
        return inviteCollection.countDocuments(PlayerInvite::inviter eq inviter)
    }

    suspend fun addPlayerInvite(invited: UUID, inviter: UUID) {
        inviteCollection.insertOne(PlayerInvite(inviter, invited).apply { timestamp = Clock.System.now() })
    }

    suspend fun removePlayerInvite(invited: UUID) {
        inviteCollection.deleteOne(PlayerInvite::invited eq invited)
    }

    suspend fun hasPlayerReceivedInvite(invited: UUID): Boolean {
        return getPlayerInvite(invited) != null
    }

    private const val inviteLimit = 1
    suspend fun canInvitePlayers(arcadeSender: ArcadeSender): Boolean {
        return canInviteInfinitePlayers(arcadeSender) || countPlayerInvitesByPlayer(arcadeSender.uuid) < inviteLimit
    }

    private fun canInviteInfinitePlayers(arcadeSender: ArcadeSender) =
        arcadeSender.hasAtLeastRank(HypixelPackageRank.ADMIN, true)

    suspend fun getRemainingInvites(arcadeSender: ArcadeSender): Long {
        if (canInviteInfinitePlayers(arcadeSender)) return -1
        return inviteLimit - countPlayerInvitesByPlayer(arcadeSender.uuid)
    }
}