package io.github.nickacpt.nickarcade.utils.profiles

import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.events.impl.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.actualPlayerProfile
import org.bukkit.entity.Player


suspend fun Player.getDisplayProfile(): DumpedProfile? {
    return getPlayerData().displayOverrides.displayProfile
}

suspend inline fun reloadProfile(
    data: PlayerData,
    reloadProfile: Boolean = false,
    code: suspend PlayerData.() -> Unit
) {
    if (reloadProfile) PlayerDataLeaveEvent(data, true).callEvent()
    code(data)
    if (reloadProfile) PlayerDataJoinEvent(data, true).callEvent()
    if (reloadProfile) PlayerDataReloadEvent(data).callEvent()
}

suspend fun Player.setDisplayProfile(profile: DumpedProfile?, reloadProfile: Boolean = false) {
    reloadProfile(getPlayerData(), reloadProfile) {
        actualPlayerProfile = profile?.asPlayerProfile(uniqueId)
        setPlayerListName(null)
        displayOverrides.displayProfile = profile
    }
}
