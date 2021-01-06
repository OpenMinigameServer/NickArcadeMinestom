package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.Command
import cloud.commandframework.arguments.StaticArgument
import cloud.commandframework.internal.CommandRegistrationHandler
import io.github.nickacpt.nickarcade.utils.pluginInstance
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import java.util.*

object MinestomCommandRegistrationHandler : CommandRegistrationHandler {
    override fun registerCommand(command: Command<*>): Boolean {
        command as Command<CommandSender>

        /* We only care about the root command argument */
        val commandArgument = command.arguments[0]

        val label = commandArgument.name
        val aliases: List<String> = ArrayList((commandArgument as StaticArgument<*>).alternativeAliases)

        val commandInstance = MinecraftServer.getCommandManager().getCommand(label)
        //Same root command, register as sub-syntax
        if (commandInstance is MinestomCloudCommand<*>) {
            commandInstance.registerCommandArguments(command)
            return true
        }

        MinecraftServer.getCommandManager().register(
            MinestomCloudCommand(
                command,
                pluginInstance.commandManager.manager,
                label,
                *aliases.toTypedArray()
            )
        )

        return true
    }
}