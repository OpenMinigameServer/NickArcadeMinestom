package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.CommandTree
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.commands.parsers.PlayerDataParser
import io.leangen.geantyref.TypeToken
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.*
import java.util.function.Function

class NickArcadeCommandManager<C>(
    owningPlugin: @NonNull Plugin,
    commandExecutionCoordinator: @NonNull Function<CommandTree<C>, CommandExecutionCoordinator<C>>,
    commandSenderMapper: @NonNull Function<CommandSender, C>,
    backwardsCommandSenderMapper: @NonNull Function<C, CommandSender>
) : PaperCommandManager<C>(
    owningPlugin,
    commandExecutionCoordinator,
    commandSenderMapper,
    backwardsCommandSenderMapper
) {
    init {
        registerPlayerDataParser()
    }

    inline fun <reified T> registerParser(
        crossinline parser: suspend (CommandContext<C>, Queue<String>) -> ArgumentParseResult<T>
    ) {
        parserRegistry.registerParserSupplier(TypeToken.get(T::class.java)) {
            return@registerParserSupplier ArgumentParser<C, T> { commandContext, inputQueue ->
                runBlocking {
                    parser(
                        commandContext,
                        inputQueue
                    )
                }
            }
        }
    }

    inline fun <reified T> registerParser(
        crossinline parser: suspend (CommandContext<C>, Queue<String>) -> ArgumentParseResult<T>,
        crossinline suggestions: suspend (commandContext: CommandContext<C>, input: String) -> MutableList<String>
    ) {
        parserRegistry.registerParserSupplier(TypeToken.get(T::class.java)) {
            return@registerParserSupplier object : ArgumentParser<C, T> {
                override fun parse(
                    commandContext: @NonNull CommandContext<@NonNull C>,
                    inputQueue: @NonNull Queue<@NonNull String>
                ): @NonNull ArgumentParseResult<@NonNull T> {
                    return runBlocking { parser(commandContext, inputQueue) }
                }

                override fun suggestions(commandContext: CommandContext<C>, input: String): MutableList<String> {
                    return runBlocking { suggestions(commandContext, input) }
                }
            }
        }
    }

    private fun registerPlayerDataParser() {
        parserRegistry.registerParserSupplier(TypeToken.get(PlayerData::class.java)) { PlayerDataParser() }
    }

}