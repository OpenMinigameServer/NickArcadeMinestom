package io.github.nickacpt.nickarcade.events

import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.events.impl.game.PlayerJoinGameEvent
import io.github.nickacpt.nickarcade.events.impl.game.PlayerLeaveGameEvent
import io.github.nickacpt.nickarcade.events.impl.party.PartyPlayerLeaveEvent
import io.github.nickacpt.nickarcade.events.impl.party.PartySettingChangedEvent
import io.github.nickacpt.nickarcade.party.model.PartySettings
import io.github.nickacpt.nickarcade.utils.RanksHelper
import io.github.nickacpt.nickarcade.utils.event
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer

fun registerGameEvents() {
    event<PartyPlayerLeaveEvent> {
        if (player.displayOverrides.isPartyDisguise) {
            player.displayOverrides.resetDisguise()
            PlayerDataManager.reloadProfile(player)
        }
    }

    event<PartySettingChangedEvent<Boolean>> {

        val toggleMessage = if (newValue) "enabled" else "disabled"
        val colour = if (newValue) NamedTextColor.GREEN else NamedTextColor.RED

        party.audience.sendMessage(
            separator {
                append(text(player.getChatName(actualData = true, colourPrefixOnly = false)))
                append(text(" has $toggleMessage ${setting.description}", colour))
            }
        )

    }

    event<PartySettingChangedEvent<Boolean>>(forceBlocking = true) {
        if (prop == PartySettings::allNick) {
            party.membersList.map { it.player }.forEach {
                val hasDisguise = it.displayOverrides.displayProfile != null
                if (hasDisguise && newValue) return@forEach
                if (newValue) {
                    it.displayOverrides.apply {
                        displayProfile = ProfilesManager.profiles.random()
                        overrides = RanksHelper.randomPlayerOverrides()
                        isPartyDisguise = true
                    }
                } else if (it.displayOverrides.isPartyDisguise) {
                    it.displayOverrides.resetDisguise()
                }

                PlayerDataManager.reloadProfile(it)
            }
        }
    }

    event<PlayerJoinGameEvent>(forceBlocking = true) {
        game.audience.sendMessage(text {
            it.append(text(player.getChatName(false, colourPrefixOnly = true)))
            it.append(text(" has joined (", NamedTextColor.YELLOW))
            it.append(text(playerCount, NamedTextColor.AQUA))
            it.append(text("/", NamedTextColor.YELLOW))
            it.append(text(game.maxPlayerCount, NamedTextColor.AQUA))
            it.append(text(")!", NamedTextColor.YELLOW))
        })
    }
    event<PlayerLeaveGameEvent> {
        game.audience.sendMessage(text {
            it.append(text(player.getChatName(false, colourPrefixOnly = true)))
            it.append(text(" has quit!", NamedTextColor.YELLOW))
        })
        val minestomPlayer = player.player ?: return@event
        if (lobbyInstance != minestomPlayer.instance)
            minestomPlayer.setInstance(lobbyInstance)

        if (game.playerCount == 0) {
            //Discard game instance
            game.stopTimers()

            MinecraftServer.getInstanceManager().unregisterInstance(game.arena)
        }

        PlayerDataManager.reloadProfile(player)
    }
}