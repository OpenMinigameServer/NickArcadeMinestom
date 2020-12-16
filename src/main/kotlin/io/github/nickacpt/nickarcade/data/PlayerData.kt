package io.github.nickacpt.nickarcade.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.utils.bukkitAudiences
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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
    val cooldowns: MutableMap<String, Long> = mutableMapOf(),
    currentChannel: ChatChannelType? = null
) {
    init {
        if (rawHypixelData != null) {
            hypixelData = HypixelApi.objectMapper.treeToValue<HypixelPlayer>(rawHypixelData)
        }
    }

    val currentChannel: ChatChannelType = currentChannel ?: ChatChannelType.ALL

    @get:JsonIgnore
    val audience: Audience
        get() = bukkitAudiences.player(uuid)

    @get:JsonIgnore
    val player: Player?
        get() = Bukkit.getPlayer(uuid) ?: ImpersonationManager.getImpersonatorPlayer(uuid)

    @get:JsonIgnore
    val isOnline: Boolean
        get() = player != null

    @get:JsonIgnore
    val effectivePrefix: String
        get() = computeEffectivePrefix() ?: ""

    private fun computeEffectivePrefix(): String? {
        return overrides.prefixOverride?.let { if (!it.endsWith(' ')) "$it " else it }
            ?: if (hypixelData != null)
                hypixelData?.let { hypixelPlayer: HypixelPlayer ->
                    if (overrides.monthlyRankColorOverride != null || overrides.rankPlusColorOverride != null) {
                        return@let effectiveRank.computePrefixForPlayer(
                            overrides.rankPlusColorOverride ?: MinecraftChatColor.RED,
                            overrides.monthlyRankColorOverride ?: MinecraftChatColor.GOLD
                        )
                    } else {
                        return@let overrides.rankOverride?.computePrefixForPlayer(
                            hypixelPlayer
                        ) ?: hypixelData?.effectivePrefix
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