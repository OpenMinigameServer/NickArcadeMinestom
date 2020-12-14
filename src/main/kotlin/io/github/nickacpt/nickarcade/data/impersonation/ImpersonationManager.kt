package io.github.nickacpt.nickarcade.data.impersonation

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
}