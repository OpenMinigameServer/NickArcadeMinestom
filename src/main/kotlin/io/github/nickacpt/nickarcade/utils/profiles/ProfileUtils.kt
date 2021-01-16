package io.github.nickacpt.nickarcade.utils.profiles

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataJoinEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.data.PlayerDataReloadEvent
import io.github.nickacpt.nickarcade.utils.actualPlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.callEvent
import net.minestom.server.entity.Player


suspend fun Player.getDisplayProfile(): DumpedProfile? {
    return (getArcadeSender() as? ArcadePlayer)?.displayOverrides?.displayProfile
}

suspend inline fun reloadProfile(
    data: ArcadePlayer,
    reloadProfile: Boolean = false,
    rejoinProfile: Boolean = false,
    code: suspend ArcadePlayer.() -> Unit
) {
    if (rejoinProfile && reloadProfile) PlayerDataLeaveEvent(data, true).callEvent()
    code(data)
    if (rejoinProfile && reloadProfile) PlayerDataJoinEvent(data, true).callEvent()
    if (reloadProfile) PlayerDataReloadEvent(data).callEvent()
}

suspend fun Player.setDisplayProfile(profile: DumpedProfile?, reloadProfile: Boolean = false) {
    reloadProfile(getArcadeSender(), reloadProfile) {
        actualPlayerProfile = profile?.asPlayerProfile()
        setDisplayName(null)
        displayOverrides.displayProfile = profile
    }
}
