package io.github.nickacpt.nickarcade.commands

import cloud.commandframework.annotations.CommandMethod
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.utis.profile.Profile
import io.github.nickacpt.hypixelapi.utis.profile.ProfileApi
import io.github.nickacpt.nickarcade.data.player.ArcadePlayer
import io.github.nickacpt.nickarcade.data.player.PlayerOverrides
import io.github.nickacpt.nickarcade.data.player.getArcadeSender
import io.github.nickacpt.nickarcade.display.NickContext
import io.github.nickacpt.nickarcade.events.toPlayerProfile
import io.github.nickacpt.nickarcade.events.validNamePattern
import io.github.nickacpt.nickarcade.utils.*
import io.github.nickacpt.nickarcade.utils.commands.RequiredRank
import io.github.nickacpt.nickarcade.utils.interop.PlayerProfile
import io.github.nickacpt.nickarcade.utils.interop.ProfileProperty
import io.github.nickacpt.nickarcade.utils.interop.getLastColors
import io.github.nickacpt.nickarcade.utils.interop.launch
import io.github.nickacpt.nickarcade.utils.profiles.DumpedProfile
import io.github.nickacpt.nickarcade.utils.profiles.ProfilesManager
import io.github.nickacpt.nickarcade.utils.profiles.setDisplayProfile
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import java.util.*
import kotlin.random.Random

object NickCommand {

    private val steveProfile by lazy {
        PlayerProfile(
            UUID.fromString("c06f8906-4c8a-4911-9c29-ea1dbd1aab82"),
            name = "skin902537698",
            properties = mutableListOf(
                ProfileProperty(
                    "textures",
                    "ewogICJ0aW1lc3RhbXAiIDogMTYxMDMwNjk2Njg2MCwKICAicHJvZmlsZUlkIiA6ICJjMDZmODkwNjRjOGE0OTExOWMyOWVhMWRiZDFhYWI4MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3RldmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0YWY3MTg0NTVkNGFhYjUyOGU3YTYxZjg2ZmEyNWU2YTM2OWQxNzY4ZGNiMTNmN2RmMzE5YTcxM2ViODEwYiIKICAgIH0KICB9Cn0=",
                    "s+BUn/4Jom5LHFxYeoaUGDiok6xf3r+l6uh2Sv94zqFtUlYGP7cCTzCRqnwq1qGajKKYVGTY30tZ2hwcKtcS8mxkcqS8d2NHwpaFE0MHPWnGxg/7IIPBboYnWFTulWEeoQfl4+b+Q5HPo1+YV7rkM5mQ3+5Wo+h1TQzSUK+YtG3MOHPsYFJH4v3datWBqW9dG0PxPIx75l07RRGxpNYAZFKoptT06bdntuV5N0RoW4ZL0YtOi4vb06hy6Bl+pp7eilhftupQIc0PWCbT1C+VbiihlLDw/xCriDJxK8a1TxPME0AUAAEO6DlCpSzj3ge8zvv0sSHu58JTcdiolrP2r47gnMJ9U/6AXLaBT64Lcb2HWUMGXLeQP2W9c/NiHYxhgU7G1U/GZcwUFHf5iqtCsqQzTWiRehkMIoyzIGIs/IDxfFTESxhTx8uTqp3Fk+e/dAU7L7hHXLDhs20BJva4NnYVL/FWdIxdeg1Uta+loG96nPfaARvY/dZEO88gNc3JCvAqwv3RJJsWDXvr6rj2IoPtloo3Clpw0k6dx04rV0zGyTgE+fhSuQSH1IqFyhCfZYItrmP9Q8HhoI0ZYlCD3mxY2a91OPOVSjj3Lv8ewl2h3bXcDtDnfPuDS9xaoVEyABrOugA78UVwi6QgmSr+Vm9XTcv4wNFs1AAZVkcnxP0="
                )
            )
        )
    }

    private val alexProfile by lazy {
        PlayerProfile(
            UUID.fromString("c06f8906-4c8a-4911-9c29-ea1dbd1aab82"),
            name = "skin396213",
            properties = mutableListOf(
                ProfileProperty(
                    "textures",
                    "eyJ0aW1lc3RhbXAiOjE1NDAwODc3MDUyNzYsInByb2ZpbGVJZCI6IjZhYjQzMTc4ODlmZDQ5MDU5N2Y2MGY2N2Q5ZDc2ZmQ5IiwicHJvZmlsZU5hbWUiOiJNSEZfQWxleCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNjZWU1Y2E2YWZjZGIxNzEyODVhYTAwZTgwNDljMjk3YjJkYmViYTBlZmI4ZmY5NzBhNTY3N2ExYjY0NDAzMiIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19",
                    "k62gX82u1JBsUMXPAzADWTlpzrBNti01wwnJ+l9TnNP7PuZ7dvlHztAsIr3H4EdBWmyjq3lpdWRgRkuQ0BiFai/cNuqTJBRJENIXH0pdNQnLBkyJmGsFAYHrufEtUr0ikSF2afjAY65wH6v3iCiMofnEXuzB/xrhjIlGl83LCeTLSWJ36d321XCiP2hJ/LF9a6UY7x4Po2qSzFyC5naqm84+BaTzRlnhX5spJjkexJ5N9APhcmzzALybwEfCXmeRMct+8s8qTdmHPm9H8hNg+FxsOekSPgcbZTaLmz8j1tfiX0SBPPhjPNBA604pP53a9ZcOt+6eMJXWy79i9BIlScua4iC5BT7WXitck5h+kNT0mBmMt+YEcbw0VVAVQzE29+MPC+QmrLky1vqZoXb/wsZbnZmbD6npR3b+Fnd0wNW5u83P0ssRUDzqDhPAryPayzDLQ+jLP1GSMA12etFcOE7cFkyv2H1Tz/U9iLJ0kug19dCq6GBB4EkXS51VKbCD23e2gbHwogsW0gzAKiu9d4IcA9lRUB9QMGbHQNzJnD+nU9uxGFryrXx2q0VOcaszmbuC0IKyWSxLtDenO5CBng5Wwlwtmcg9fkqawMhUAuT8+7vyBzV/Oj0gtVCxLEm/cxkXBW6O833p4CmQfXd0Ry2y73GMt2FPHFQgAFS7Q/w="
                )
            )
        )
    }

    private suspend fun showPageOne(player: ArcadePlayer, context: NickContext = NickContext()) {
        if (context.acceptedTerms == true) {
            showPageTwo(player, context)
            return
        }
        val page = text {
            it.append(text("Nicknames allow you to play with a different username to not get recognized."))
                .append(newline())
            it.append(newline())
            it.append(text("All rules still apply.")).append(newline())
            it.append(text("You can still be reported and all name history is stored."))
                .append(newline())
            it.append(newline())
            it.append(
                text(
                    "➤ I understand, set up my nickname",
                    Style.style(TextDecoration.UNDERLINED)
                )
                    .hoverEvent(
                        text("Click here to proceed")
                    )
                    .clickEvent {
                        context.acceptedTerms = true
                        showPageTwo(player, context)
                    }
            )
        }

        player.player?.openBook(page)
    }

    private suspend fun showPageTwo(player: ArcadePlayer, context: NickContext = NickContext()) {
        val isYoutuber = player.hasAtLeastRank(HypixelPackageRank.YOUTUBER, true)

        val page = text { page ->
            page.append(text("Let's get you set up with your nickname!"))
                .append(newline())
            page.append(text {
                it.append(text("First, you'll need to choose which "))
                it.append(text("RANK", Style.style(TextDecoration.BOLD)))
                it.append(text(" you would like to be shown as when nicked.").append(newline()))
            })
                .append(newline())

            val ranks = mutableListOf(
                HypixelPackageRank.NONE,
                HypixelPackageRank.VIP,
                HypixelPackageRank.VIP_PLUS,
                HypixelPackageRank.MVP,
                HypixelPackageRank.MVP_PLUS,
            )
            if (isYoutuber)
                ranks.add(HypixelPackageRank.SUPERSTAR)

            ranks.forEach { rank ->
                page.append(text { rankText ->
                    val color = getLastColors(rank.defaultPrefix)
                    var name = (rank.defaultPrefix).dropWhile { it != '[' }.trim().removeSurrounding("[", "]")
                    if (rank == HypixelPackageRank.NONE) {
                        name = "DEFAULT"
                    }

                    val fullName = color + name
                    rankText
                        .append(text("➤ "))
                        .append(text(fullName))
                        .append(newline()).hoverEvent(text("Click here to be shown as $fullName"))
                }.clickEvent {
                    context.rank = rank
                    showPageThree(player, context)
                })
            }
        }

        player.player?.openBook(page)
    }

    private suspend fun showPageThree(player: ArcadePlayer, context: NickContext = NickContext()) {
        val isYoutuber = player.hasAtLeastRank(HypixelPackageRank.YOUTUBER, true)
        val page = text { page ->
            page.append(text("Awesome! Now, which ")).append(text("SKIN", Style.style(TextDecoration.BOLD)))
                .append(text(" would you like to have while nicked?")).append(newline())
                .append(newline())

            page.append(
                text("➤ My normal skin").hoverEvent(
                    text("Click here to use your normal skin")
                        .append(newline())
                        .append(text("WARNING: ", RED))
                        .append(text("Players will be able to know who you are if you use this option."))

                ).clickEvent {
                    setContextSkinAndMoveToPageFour(
                        context,
                        player.player!!.getPlayerProfile()!!
                            .copy(name = getRandomProfileName(), uuid = UUID.randomUUID())
                    )
                }.append(newline())
            )

            page.append(
                text("➤ Steve/Alex skin").hoverEvent(
                    text("Click here to use a Steve/Alex skin")
                ).clickEvent {
                    setContextSkinAndMoveToPageFour(
                        context,
                        (if (Random.nextBoolean()) steveProfile else alexProfile).copy(
                            uuid = UUID.randomUUID(),
                            name = getRandomProfileName()
                        )
                    )
                }.append(newline())
            )

            if (isYoutuber) {
                page.append(
                    text("➤ Use a Minecraft user skin").hoverEvent(
                        text("Click here to use pick a skin from a name")
                    ).clickEvent {
                        player.audience.sendMessage(
                            text(
                                "Please type a name of the skin that you want to use in chat.",
                                GREEN
                            )
                        )
                        ChatInput.requestInput(this, onSuccess = { name ->
                            pluginInstance.launch {
                                val skin = ProfilesManager.profiles.firstOrNull { it.name == name }
                                    ?: ProfileApi.getProfileByName(name)?.takeIf { !it.isError }?.toDumpedProfile()
                                if (skin != null) {
                                    pluginInstance.launch {
                                        setContextSkinAndMoveToPageFour(
                                            context,
                                            skin.asPlayerProfile()
                                        )
                                    }
                                } else {
                                    player.audience.sendMessage(
                                        text(
                                            "There is no player with that name!",
                                            RED
                                        )
                                    )
                                }
                            }
                        })

                    }.append(newline())
                )
            }

            val profile = player.displayOverrides.displayProfile
            if (profile != null) {
                page.append(
                    text("➤ Reuse current /nick skin ('${profile.name}')").hoverEvent(
                        text("Click here to reuse the current /nick skin")
                    ).clickEvent {
                        setContextSkinAndMoveToPageFour(
                            context, profile.asPlayerProfile()
                        )
                    }.append(newline())
                )
            }

            page.append(
                text("➤ Random skin").hoverEvent(
                    text("Click here to use a random preset skin")
                ).clickEvent {
                    setContextSkinAndMoveToPageFour(
                        context, ProfilesManager.profiles.random().copy()
                            .asPlayerProfile()
                    )
                }.append(newline())
            )
        }

        player.player?.openBook(page)
    }

    private fun getRandomProfileName() = ProfilesManager.profiles.random().name

    private suspend fun showPageFour(player: Player, context: NickContext = NickContext()) {
        val isYoutuber = player.getArcadeSender().hasAtLeastRank(HypixelPackageRank.YOUTUBER, true)
        val page = text { page ->
            page.append(text("Alright, now you'll need to choose the "))
                .append(text("NAME", Style.style(TextDecoration.BOLD)))
                .append(text(" to use!")).append(newline())
                .append(newline())

            if (isYoutuber) {
                page.append(
                    text("➤ Enter a name").hoverEvent(
                        text("Click to enter the name to use")
                    ).clickEvent {
                        player.asAudience.sendMessage(
                            text(
                                "Please type the name you want to use in chat.",
                                NamedTextColor.GOLD
                            )
                        )
                        ChatInput.requestInput(player, onSuccess = {
                            setContextNameAndFinish(context, it)
                        }, isValid = {
                            return@requestInput validNamePattern.matchEntire(it) != null
                        })
                    }.append(newline())
                )
            }
            page.append(
                text("➤ Use a random name").hoverEvent(
                    text("Click to use a randomly generated name")
                ).clickEvent {
                    showPageFour(player, context.apply { skin?.name = getRandomProfileName() })
                }.append(newline())
            )

            val name = context.skin?.name ?: throw Exception("Unable to find skin name")
            page.append(
                text("➤ Use skin name '$name'").hoverEvent(
                    text("Click here to use the name associated with the picked skin.")
                ).clickEvent {
                    setContextNameAndFinish(context, name)
                }.append(newline())
            ).append(newline())

            page.append(
                text("To go back to being your usual self, type:")
                    .append(newline())
                    .append(text("/unnick", Style.style(TextDecoration.BOLD)))
            )
        }

        player.openBook(page)
    }

    private fun Player.setContextNameAndFinish(context: NickContext, name: String) {
        val player = this
        context.skin?.name = name
        pluginInstance.launch {
            player.applyNickContext(context)
        }
    }

    private suspend fun Player.setContextSkinAndMoveToPageFour(context: NickContext, skin: PlayerProfile) {
        context.skin = skin
        showPageFour(this, context)
    }

    private fun Player.openBook(page: Component) {
        asAudience.openBook(Book.book(text("NickArcade"), text("NickAc"), page))
    }

    @RequiredRank(HypixelPackageRank.SUPERSTAR)
    @CommandMethod("nick")
    fun nickCommand(sender: ArcadePlayer) = command(sender) {
        val context = NickContext()
        if (sender.hasAtLeastRank(HypixelPackageRank.YOUTUBER, true)) {
            context.acceptedTerms = true
        }
        showPageOne(sender, context)
    }

    @RequiredRank(HypixelPackageRank.SUPERSTAR)
    @CommandMethod("unnick")
    fun unnickCommand(sender: ArcadePlayer) = command(sender) {
        sender.player?.applyNickContext(null)
    }

}

private fun Profile.toDumpedProfile(): DumpedProfile {
    return toPlayerProfile().toDumpedProfile()
}

private suspend fun Player.applyNickContext(context: NickContext?) {
    val player = getArcadeSender()
    val minestomPlayer = player.player ?: return
    if (context == null) {
        player.displayOverrides.resetDisguise()
        minestomPlayer.setDisplayProfile(null, true)
        player.audience.sendMessage(text("Your nick has been reset!", GREEN))
        return
    }

    val profile = context.skin!!.toDumpedProfile()

    player.displayOverrides.isPartyDisguise = false
    player.displayOverrides.displayProfile = profile

    player.displayOverrides.overrides =
        PlayerOverrides(
            context.rank,
            networkLevel = Random.nextInt(1, 50).toLong(),
            isLegacyPlayer = false
        )
    minestomPlayer.setDisplayProfile(profile, true)
    player.audience.sendMessage(text("You are now nicked as ${profile.name}!", GREEN))
}
