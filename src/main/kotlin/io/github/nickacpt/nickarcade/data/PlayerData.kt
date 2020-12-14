package io.github.nickacpt.nickarcade.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*

data class PlayerOverrides(
    var rankOverride: HypixelPackageRank? = null,
    var prefixOverride: String? = null,
    var monthlyRankColorOverride: MinecraftChatColor? = null,
    var rankPlusColorOverride: MinecraftChatColor? = null
)

class PlayerData(
    @JsonProperty("_id") val uuid: UUID,
    @JsonIgnore var hypixelData: HypixelPlayer?,
    val overrides: PlayerOverrides = PlayerOverrides(),
    val rawHypixelData: JsonNode? = hypixelData?.rawJsonNode,
    val cooldowns: MutableMap<String, Long> = mutableMapOf()
) {
    init {
        if (rawHypixelData != null) {
            hypixelData = HypixelApi.objectMapper.treeToValue<HypixelPlayer>(rawHypixelData)
        }
    }

    @get:JsonIgnore
    val effectivePrefix: String
        get() = computeEffectivePrefix() ?: ""

    private fun computeEffectivePrefix(): String? {
        return overrides.prefixOverride?.let { if (!it.endsWith(' ')) "$it " else it }
            ?: if (hypixelData != null)
                hypixelData?.let {
                    val prefixForPlayer = overrides.rankOverride?.computePrefixForPlayer(
                        it
                    )
                    if (prefixForPlayer != null) {
                        return@let prefixForPlayer
                    } else {
                        if (overrides.monthlyRankColorOverride != null || overrides.rankPlusColorOverride != null) {
                            return@let it.effectiveRank.computePrefixForPlayer(
                                overrides.rankPlusColorOverride ?: MinecraftChatColor.RED,
                                overrides.rankPlusColorOverride ?: MinecraftChatColor.GOLD
                            )
                        }
                        return@let hypixelData?.effectivePrefix
                    }
                } else null
    }


    @get:JsonIgnore
    val displayName: String
        get() = hypixelData?.displayName ?: ""

    @get:JsonIgnore
    val effectiveRank: HypixelPackageRank
        get() = overrides.rankOverride ?: hypixelData?.effectiveRank ?: HypixelPackageRank.NONE

    fun formatChatMessage(message: String): String {
        return "${getChatName()}${effectiveRank.chatMessagePrefix}: $message"
    }

    @JsonIgnore
    fun getChatName() = "${effectivePrefix}$displayName"
    fun hasAtLeastRank(rank: HypixelPackageRank): Boolean {
        return effectiveRank.ordinal >= rank.ordinal
    }

    fun computeHoverEventComponent(): HoverEventSource<*> {
        return Component.text {
            it.run {
                append(Component.text(getChatName()))
                append(Component.newline())
                append(Component.text("Hypixel Level: ", NamedTextColor.GRAY))
                append(Component.text(hypixelData?.networkLevel ?: 0, NamedTextColor.GOLD))
            }
        }
    }
}