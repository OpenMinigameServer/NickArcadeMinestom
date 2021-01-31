package io.github.nickacpt.nickarcade.testflows.impl

import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.game.definition.ArenaDefinition
import io.github.nickacpt.nickarcade.game.definition.MiniGameType
import io.github.nickacpt.nickarcade.game.definition.position.GamePosition
import io.github.nickacpt.nickarcade.game.impl.BedWarsMode
import io.github.nickacpt.nickarcade.party.model.MemberRole
import io.github.nickacpt.nickarcade.party.model.PartySettings
import io.github.nickacpt.nickarcade.utils.interop.getOnlinePlayers

object JoinBedWarsGameImpl : TestFlowImplementation {
    val arena = ArenaDefinition(
        "Glacier",
        2,
        4,
        "glacier",
        54f,
        GamePosition(0.0, 60.0, 0.0)
    )

    override suspend fun execute(launcher: ArcadePlayer) {
        val party = launcher.getOrCreateParty()
        getOnlinePlayers().filter { it.uuid != launcher.uuid }.map { it.getArcadeSender() }.forEach {
            party.addMember(it, true, MemberRole.MEMBER)
        }

        val createGame = MiniGameManager.createGame(
            MiniGameType.BED_WARS,
            BedWarsMode.SOLO,
            arena
        )!!

        party.settings.setPropertyAndNotify(launcher, PartySettings::privateMode, true)
        party.settings.setPropertyAndNotify(launcher, PartySettings::developerMode, true)

//        delay(2.seconds)

        createGame.addPlayer(launcher)
    }
}