package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.CommandManager
import cloud.commandframework.CommandTree
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.internal.CommandRegistrationHandler
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.meta.SimpleCommandMeta
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.utils.commands.parsers.PlayerDataParser
import io.leangen.geantyref.TypeToken
import kotlinx.coroutines.runBlocking
import net.minestom.server.command.CommandSender
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.*
import java.util.function.Function

class NickArcadeCommandManager<C : CommandSender>(
    commandExecutionCoordinator: @NonNull Function<@NonNull CommandTree<C>, @NonNull CommandExecutionCoordinator<C>>,
    commandRegistrationHandler: @NonNull CommandRegistrationHandler
) : CommandManager<C>(commandExecutionCoordinator, commandRegistrationHandler) {
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

    override fun hasPermission(sender: C, permission: String): Boolean {
        if (sender.isConsole) return true
        return sender.hasPermission(permission)
    }

    override fun createDefaultCommandMeta(): CommandMeta {
        return SimpleCommandMeta.simple().build()
    }

}