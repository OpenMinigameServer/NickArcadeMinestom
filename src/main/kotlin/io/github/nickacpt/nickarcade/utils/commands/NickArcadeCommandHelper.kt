package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.utils.permission
import kotlinx.coroutines.runBlocking
import java.util.function.BiFunction


class NickArcadeCommandHelper {

    lateinit var annotationParser: AnnotationParser<ArcadeSender>
    lateinit var manager: NickArcadeCommandManager<ArcadeSender>


    fun init(): NickArcadeCommandHelper {
        val executionCoordinatorFunction =
            AsynchronousCommandExecutionCoordinator.newBuilder<ArcadeSender>().build()
        try {
            manager = NickArcadeCommandManager(
                executionCoordinatorFunction,
                { runBlocking { it.getArcadeSender() } },
                { it.commandSender }
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
                ArcadeSender::class.java,  /* Mapper for command meta instances */
                commandMetaFunction
            )

            setupRequiredRankAnnotation(annotationParser)
        } catch (e: Exception) {
            throw Exception("Failed to initialize the command manager")
        }

        return this
    }

    private fun setupRequiredRankAnnotation(annotationParser: AnnotationParser<ArcadeSender>) {
        annotationParser.registerBuilderModifier(RequiredRank::class.java, BiFunction { annotation, builder ->
            return@BiFunction builder.permission(annotation.value)
        })
    }

}