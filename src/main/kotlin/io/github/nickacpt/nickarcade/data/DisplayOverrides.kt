package io.github.nickacpt.nickarcade.data

import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.utils.profiles.DumpedProfile

data class DisplayOverrides(var displayProfile: DumpedProfile? = null, var overrides: PlayerOverrides? = null)