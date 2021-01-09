package io.github.nickacpt.nickarcade.utils.profiles

import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.actualPlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import net.minestom.server.entity.Player


suspend fun Player.getDisplayProfile(): DumpedProfile? {
    return getPlayerData().displayOverrides.displayProfile
}

suspend inline fun reloadProfile(
    data: PlayerData,
    reloadProfile: Boolean = false,
    rejoinProfile: Boolean = false,
    code: suspend PlayerData.() -> Unit
) {
    if (rejoinProfile && reloadProfile) PlayerDataLeaveEvent(data, true).callEvent()
    code(data)
    if (rejoinProfile && reloadProfile) PlayerDataJoinEvent(data, true).callEvent()
    if (reloadProfile) PlayerDataReloadEvent(data).callEvent()
}

suspend fun Player.setDisplayProfile(profile: DumpedProfile?, reloadProfile: Boolean = false) {
    reloadProfile(getPlayerData(), reloadProfile) {
        actualPlayerProfile = profile?.asPlayerProfile()
        setDisplayName(null)
        displayOverrides.displayProfile = profile
    }
}
