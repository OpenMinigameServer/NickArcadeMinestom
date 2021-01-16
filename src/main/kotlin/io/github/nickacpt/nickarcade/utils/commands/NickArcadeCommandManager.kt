package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.CommandTree
import cloud.commandframework.execution.CommandExecutionCoordinator
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.utils.commands.parsers.PlayerDataParser
import io.github.openminigameserver.cloudminestom.MinestomCommandManager
import io.leangen.geantyref.TypeToken
import net.minestom.server.command.CommandSender
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.function.Function

class NickArcadeCommandManager<C>(
    commandExecutionCoordinator: @NonNull Function<@NonNull CommandTree<C>, @NonNull CommandExecutionCoordinator<C>>,
    commandSenderMapper: Function<CommandSender, C>, backwardsCommandSenderMapper: Function<C, CommandSender>
) : MinestomCommandManager<C>(commandExecutionCoordinator, commandSenderMapper, backwardsCommandSenderMapper) {
    init {
        registerPlayerDataParser()
    }

    private fun registerPlayerDataParser() {
        parserRegistry.registerParserSupplier(TypeToken.get(ArcadePlayer::class.java)) { PlayerDataParser() }
    }

}