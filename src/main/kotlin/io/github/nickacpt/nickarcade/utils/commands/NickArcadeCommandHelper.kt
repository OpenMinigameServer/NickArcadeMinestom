package io.github.nickacpt.nickarcade.utils.commands

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import io.github.nickacpt.nickarcade.utils.permission
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.BiFunction


class NickArcadeCommandHelper(private val plugin: JavaPlugin) {

    lateinit var annotationParser: AnnotationParser<CommandSender>
    lateinit var manager: NickArcadeCommandManager<CommandSender>


    fun init(): NickArcadeCommandHelper? {
        val executionCoordinatorFunction =
            AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().build()
        try {
            val commandSenderMapper: (t: CommandSender) -> CommandSender = { it }
            manager = NickArcadeCommandManager(
                plugin,
                executionCoordinatorFunction,
                commandSenderMapper,
                commandSenderMapper
            )
            if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                manager.registerBrigadier()
            }
            if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) manager.registerAsynchronousCompletions()

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
            plugin.logger.severe("Failed to initialize the command manager")
            plugin.server.pluginManager.disablePlugin(plugin)
            return null
        }

        return this
    }

    private fun setupRequiredRankAnnotation(annotationParser: AnnotationParser<CommandSender>) {
        annotationParser.registerBuilderModifier(RequiredRank::class.java, BiFunction { annotation, builder ->
            return@BiFunction builder.permission(annotation.value)
        })
    }
}