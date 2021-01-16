package io.github.nickacpt.nickarcade.data

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.utils.profiles.DumpedProfile

data class DisplayOverrides(
    var displayProfile: DumpedProfile? = null,
    var overrides: PlayerOverrides? = null,
    @JsonIgnore var isProfileOverridden: Boolean = false,
    var isPartyDisguise: Boolean = false
) {
    @JsonIgnore
    fun resetDisguise() {
        displayProfile = null
        overrides = null
        isPartyDisguise = false
    }
}