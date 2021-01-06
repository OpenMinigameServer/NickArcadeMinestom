package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import io.github.nickacpt.nickarcade.utils.permission
import net.minestom.server.command.CommandSender
import java.util.function.BiFunction


class NickArcadeCommandHelper {

    lateinit var annotationParser: AnnotationParser<CommandSender>
    lateinit var manager: NickArcadeCommandManager<CommandSender>


    fun init(): NickArcadeCommandHelper {
        val executionCoordinatorFunction =
            AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().build()
        try {
            val commandSenderMapper: (t: CommandSender) -> CommandSender = { it }
            manager = NickArcadeCommandManager(
                executionCoordinatorFunction,
                MinestomCommandRegistrationHandler
            )

            val commandMetaFunction =
                { p: ParserParameters ->
                    SimpleCommandMeta.builder() // This will allow you to decorate commands with descriptions
                        .with(
                            SimpleCommandMeta.DESCRIPTION,
                            p.get(
                                StandardParameters.DESCRIPTION,
                                "No description was provided for this command"
                            )
                        )
                        .build()
                }
            annotationParser = AnnotationParser( /* Manager */
                manager,  /* Command sender type */
                CommandSender::class.java,  /* Mapper for command meta instances */
                commandMetaFunction
            )

            setupRequiredRankAnnotation(annotationParser)
        } catch (e: Exception) {
            throw Exception("Failed to initialize the command manager")
        }

        return this
    }

    private fun setupRequiredRankAnnotation(annotationParser: AnnotationParser<CommandSender>) {
        annotationParser.registerBuilderModifier(RequiredRank::class.java, BiFunction { annotation, builder ->
            return@BiFunction builder.permission(annotation.value)
        })
    }

}