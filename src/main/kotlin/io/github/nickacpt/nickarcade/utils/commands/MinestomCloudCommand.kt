package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.Command
import cloud.commandframework.CommandManager
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.StaticArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.exceptions.*
import cloud.commandframework.execution.CommandResult
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.nickarcade.utils.asAudience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.CommandSyntax
import net.minestom.server.command.builder.arguments.*
import net.minestom.server.command.builder.arguments.minecraft.SuggestionType
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.concurrent.CompletionException
import java.util.function.BiConsumer
import net.minestom.server.command.builder.Command as MinestomCommand


class MinestomCloudCommand<C : CommandSender>(
    val command: Command<C>,
    private val manager: CommandManager<C>,
    name: String,
    vararg aliases: String
) : MinestomCommand(name, *aliases) {

    init {
        registerCommandArguments(command)
    }

    private var isAmbiguous: Boolean = false
    private val emptyExecutor = CommandExecutor { _, _ -> }

    fun <C> registerCommandArguments(cloudCommand: Command<C>) {
        if (isAmbiguous) return
        defaultExecutor = emptyExecutor
        val arguments = cloudCommand.arguments.drop(1).mapIndexedNotNull { i, arg ->
            convertCloudArgumentToMinestom(arg, i)
        }.toTypedArray()

        val containsSyntax = syntaxes.any { syntax ->
            arguments.map { it.id }.toList() == syntax.arguments.map { it.id }
        }

        val mutableList = this.syntaxes as MutableList<CommandSyntax>
        if (!containsSyntax && arguments.isNotEmpty()) {
            addSyntax(emptyExecutor, *arguments)

            val toMove = mutableList.filter { it.arguments.all { arg -> arg is ArgumentDynamicWord } }
            mutableList.removeAll(toMove)
            mutableList.addAll(0, toMove)
        }

        fixSyntaxArguments(mutableList)
    }

    private fun fixSyntaxArguments(mutableList: MutableList<CommandSyntax>) {
        isAmbiguous = mutableList.any { it.arguments.indexOfFirst { arg -> arg is ArgumentDynamicWord } == 0 }
        if (isAmbiguous) {
            mutableList.clear()
            addSyntax(emptyExecutor, ArgumentDynamicStringArray("args"))
        }
    }

    private fun <C> convertCloudArgumentToMinestom(arg: CommandArgument<@NonNull C, *>, i: Int): Argument<*> {
        return when (arg) {
            is StaticArgument -> {
                (ArgumentWord(arg.name).from(*arg.aliases.toTypedArray()))
            }
            else -> {
                val parser = arg.parser
                when {
                    arg is StringArgument && arg.stringMode == StringArgument.StringMode.GREEDY -> {
                        ArgumentStringArray(arg.name)
                    }
                    parser is StringArgument.StringParser && parser.stringMode == StringArgument.StringMode.GREEDY -> {
                        ArgumentStringArray(arg.name)
                    }
                    else -> {
                        ArgumentDynamicWord(arg.name, SuggestionType.ASK_SERVER).setDefaultValue(arg.defaultValue)
                    }
                }
            }
        }
    }

    override fun onDynamicWrite(sender: CommandSender, text: String): Array<String> {
        return manager.suggest(sender as C, text.removePrefix("/")).toTypedArray()
    }


    private val MESSAGE_INTERNAL_ERROR =
        Component.text("An internal error occurred while attempting to perform this command.", NamedTextColor.RED)
    private val MESSAGE_NO_PERMS = Component.text(
        "I'm sorry, but you do not have permission to perform this command. "
                + "Please contact the server administrators if you believe that this is in error.", NamedTextColor.RED
    )
    private val MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type \"/help\" for help."

    override fun globalListener(commandSender: CommandSender, arguments: Arguments, command: String) {
        val result = manager.executeCommand(commandSender as C, command.removePrefix("/"))

        result
            .whenComplete { _: CommandResult<C>?, throwable: Throwable? ->
                var myThrowable = throwable
                if (myThrowable != null) {
                    if (myThrowable is CompletionException) {
                        myThrowable = myThrowable.cause
                    }
                    when (myThrowable) {
                        is InvalidSyntaxException -> {
                            manager.handleException(commandSender,
                                InvalidSyntaxException::class.java,
                                myThrowable,
                                BiConsumer { _: C, _: InvalidSyntaxException? ->
                                    commandSender.sendMessage(
                                        MinecraftChatColor.RED.toString() + "Invalid Command Syntax. "
                                                + "Correct command syntax is: "
                                                + MinecraftChatColor.GRAY + "/"
                                                + (myThrowable as InvalidSyntaxException?)
                                            ?.correctSyntax
                                    )
                                }
                            )
                        }
                        is InvalidCommandSenderException -> {
                            manager.handleException(commandSender,
                                InvalidCommandSenderException::class.java,
                                myThrowable,
                                BiConsumer { _: C, _: InvalidCommandSenderException? ->
                                    commandSender.sendMessage(
                                        MinecraftChatColor.RED.toString() + myThrowable.message
                                    )
                                }
                            )
                        }
                        is NoPermissionException -> {
                            manager.handleException(commandSender,
                                NoPermissionException::class.java,
                                (myThrowable as NoPermissionException?)!!,
                                { _: C, _ ->
                                    commandSender.asAudience.sendMessage(
                                        MESSAGE_NO_PERMS
                                    )
                                }
                            )
                        }
                        is NoSuchCommandException -> {
                            manager.handleException(commandSender,
                                NoSuchCommandException::class.java,
                                myThrowable,
                                BiConsumer { _: C, _: NoSuchCommandException? ->
                                    commandSender.sendMessage(
                                        MESSAGE_UNKNOWN_COMMAND
                                    )
                                }
                            )
                        }
                        is ArgumentParseException -> {
                            manager.handleException(commandSender,
                                ArgumentParseException::class.java,
                                myThrowable,
                                BiConsumer { _: C, _: ArgumentParseException? ->
                                    commandSender.sendMessage(
                                        MinecraftChatColor.RED.toString() + "Invalid Command Argument: "
                                                + MinecraftChatColor.GRAY + myThrowable.cause
                                            .message
                                    )
                                }
                            )
                        }
                        is CommandExecutionException -> {
                            manager.handleException(commandSender,
                                CommandExecutionException::class.java,
                                (myThrowable as CommandExecutionException?)!!,
                                { _: C, _ ->
                                    commandSender.asAudience.sendMessage(MESSAGE_INTERNAL_ERROR)
                                    MinecraftServer.LOGGER.error(
                                        "Exception executing command handler",
                                        myThrowable.cause
                                    )
                                }
                            )
                        }
                        else -> {
                            commandSender.asAudience.sendMessage(MESSAGE_INTERNAL_ERROR)
                            MinecraftServer.LOGGER.error(
                                "An unhandled exception was thrown during command execution",
                                myThrowable
                            )
                        }
                    }
                }
            }
    }
}