package io.github.nickacpt.nickarcade.invite

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.datetime.Instant
import java.util.*

data class PlayerInvite(val inviter: UUID, val invited: UUID, var timestampValue: Long = 0L) {
    @get:JsonIgnore
    @set:JsonIgnore
    var timestamp: Instant
        get() {
            return Instant.fromEpochMilliseconds(timestampValue)
        }
        set(value) {
            timestampValue = value.toEpochMilliseconds()
        }
}
