package io.github.nickacpt.nickarcade.data.impersonation

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object ImpersonationManager {
    private val impersonations = mutableMapOf<UUID, ImpersonationData>()

    fun removeImpersonation(id: UUID) {
        impersonations.remove(id)
    }

    fun getImpersonation(id: UUID): ImpersonationData? {
        return impersonations[id]
    }

    fun impersonate(id: UUID, impersonation: ImpersonationData) {
        impersonations[id] = impersonation
    }

    fun getImpersonatorPlayer(uuid: UUID): Player? {
        return impersonations
            .filterValues { it.uniqueId == uuid }.keys.firstOrNull()
            ?.let { Bukkit.getPlayer(it) }
    }
}