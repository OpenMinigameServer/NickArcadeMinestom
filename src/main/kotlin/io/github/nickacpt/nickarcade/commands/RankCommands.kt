package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.Command
import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.context.CommandContext
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.data.DisplayOverrides
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.ArcadeSender
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.utils.command
import io.github.nickacpt.nickarcade.utils.commands.NickArcadeCommandHelper
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.div
import io.github.nickacpt.nickarcade.utils.permission
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.apache.commons.lang3.StringUtils
import org.checkerframework.checker.nullness.qual.NonNull
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.exists
import org.litote.kmongo.ne
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.jvmErasure

object RankCommands {

    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("ranks|rank get <player>")
    fun getPlayerInfo(
        sender: ArcadeSender,
        @Argument("player") player: ArcadePlayer
    ) = command(sender, HypixelPackageRank.ADMIN) {
        sender.audience.sendMessage {
            text {
                it.append(text(player.getChatName(true)))
                it.append(text(" is player's display on this server.", GREEN))
                if (player.displayOverrides.displayProfile != null) {
                    it.append(newline())
                    it.append(text(player.getChatName(false)))
                    it.append(text(" is player's /nick display on this server.", GREEN))
                }
            }
        }
    }


    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("ranks|rank listnicked")
    fun listNickedPlayerInfo(
        sender: ArcadeSender
    ) = command(sender, HypixelPackageRank.ADMIN) {
        val displayProfileOverrides = ArcadePlayer::displayOverrides / DisplayOverrides::displayProfile
        val players = PlayerDataManager.playerDataCollection
            .find(displayProfileOverrides ne null)
            .toList()

        sender.audience.sendMessage(text("All nicked players:", NamedTextColor.GOLD))
        sender.audience.sendMessage(text(players.joinToString("\n") { p ->
            LegacyComponentSerializer.legacySection().serialize(text {
                it.append(text(p.getChatName(true)))
                it.append(text(" is nicked with name ", YELLOW))
                it.append(text(p.getChatName(false)))
            })
        }, NamedTextColor.GOLD))

    }


    @RequiredRank(HypixelPackageRank.ADMIN)
    @CommandMethod("ranks|rank list <rank>")
    fun listPlayersRanked(
        sender: ArcadeSender,
        @Argument("rank") rank: HypixelPackageRank
    ) = command(sender, HypixelPackageRank.ADMIN) {
        val players =
            PlayerDataManager.playerDataCollection.find(
                and(
                    (ArcadePlayer::overrides / PlayerOverrides::rankOverride).exists(),
                    ArcadePlayer::overrides / PlayerOverrides::rankOverride eq rank
                )
            ).toList()

        sender.audience.sendMessage {
            text {
                it.append(text("Found ${players.size} player(s) with rank $rank:", GREEN))
                players.forEach { p ->
                    it.append(newline())
                    it.append(text(p.getChatName(true)).hoverEvent(p.computeHoverEventComponent()))
                }
            }
        }
    }

    private fun sendSuccessMessage(
        sender: ArcadeSender,
        message: @NonNull TextComponent,
        playerData: ArcadePlayer
    ) {
        sender.audience.sendMessage(
            text {
                it.append(
                    message
                )
                it.append(newline())
                it.append(computeDisplayNameMessage(playerData))
            }

        )
    }

    private fun computeDisplayNameMessage(playerData: ArcadePlayer) = text(
        "Their display name is now ",
        GREEN
    ).append(text(playerData.getChatName()))

    fun registerOverrideRanksCommands(helper: NickArcadeCommandHelper) {
        val kClass = PlayerOverrides::class
        kClass.declaredMemberProperties.forEach {
            if (it is KMutableProperty1<PlayerOverrides, *>) {
                registerPropertySubcommand(helper, it as KMutableProperty1<PlayerOverrides, Any?>)
            }
        }
    }

    private fun <T> registerPropertySubcommand(
        helper: NickArcadeCommandHelper,
        prop: KMutableProperty1<PlayerOverrides, T?>
    ) {
        val propChanged = prop.name.capitalize().replace("Override", "")
        val commandBuilder = helper.manager.commandBuilder(
            "ranks",
            "rank"
        )
        helper.manager.command(
            commandBuilder.createSetValueHandler(propChanged, prop).build()
        )
        if (prop.returnType.isMarkedNullable)
            helper.manager.command(
                commandBuilder.literal("reset$propChanged", "remove$propChanged")
                    .argument(ArcadePlayer::class.java, "player") {
                        it.asRequired()
                    }
                    .permission(HypixelPackageRank.ADMIN)
                    .handler(handleValueSet(prop, propChanged))
                    .build()
            )
    }

    private fun <T> handleValueSet(
        prop: KMutableProperty1<PlayerOverrides, T?>,
        propChanged: String,
    ): (commandContext: @NonNull CommandContext<ArcadeSender>) -> Unit =
        {
            command(it.sender, HypixelPackageRank.ADMIN) {
                val player = it.get<ArcadePlayer>("player")
                var valueFinal = it.getOrDefault<T>("value", null)
                if (valueFinal is String) {
                    valueFinal = valueFinal.replace('&', '§') as T
                }
                val originalName = player.displayName
                prop.set(player.overrides, valueFinal)
                val changed = StringUtils.join(
                    StringUtils.splitByCharacterTypeCamelCase(propChanged),
                    ' '
                )
                val message =
                    if (valueFinal != null) {
                        var valueFinalToString = valueFinal.toString()
                        if (valueFinal is Enum<*>) {
                            valueFinalToString = valueFinal.name.toLowerCase().split('_').joinToString(" ").capitalize()
                        }
                        "Successfully set $originalName's $changed to $valueFinalToString"
                    } else "Successfully reset $originalName's $changed"

                PlayerDataManager.savePlayerData(player)
                PlayerDataManager.reloadProfile(player)
                sendSuccessMessage(
                    it.sender,
                    text(message, GREEN),
                    player
                )
            }
        }

    private fun <T> Command.Builder<ArcadeSender>.createSetValueHandler(
        propChanged: String,
        prop: KMutableProperty1<PlayerOverrides, T?>
    ): @NonNull Command.Builder<ArcadeSender> {
        val javaType: Class<Any> = prop.returnType.jvmErasure.java as Class<Any>
        return this.literal("set$propChanged")
            .argument(ArcadePlayer::class.java, "player") {
                it.asRequired()
            }
            .permission(HypixelPackageRank.ADMIN)
            .argument(javaType, "value") { builder ->
                builder.asRequired()
                /*  if (javaType == HypixelPackageRank::class.java) {
                      (builder as CommandArgument.Builder<CommandSender, HypixelPackageRank>).withParser(
                          EnumArgument.EnumParser<CommandSender, HypixelPackageRank>(
                              HypixelPackageRank::class.java
                          ).also {
                              it.allowedValues = EnumSet.complementOf(EnumSet.of(HypixelPackageRank.NORMAL))
                          }
                      )
                  }*/
            }
            .handler(handleValueSet(prop, propChanged))
    }
}