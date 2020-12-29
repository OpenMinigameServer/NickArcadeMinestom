package io.github.nickacpt.nickarcade.data

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.hypixelapi.utis.MinecraftChatColor
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.party.model.Party
import io.github.nickacpt.nickarcade.utils.bukkitAudiences
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import java.util.*

data class PlayerOverrides(
    var rankOverride: HypixelPackageRank? = null,
    var prefixOverride: String? = null,
    var monthlyRankColorOverride: MinecraftChatColor? = null,
    var rankPlusColorOverride: MinecraftChatColor? = null,
    var miseryMode: Boolean? = null,
    val networkLevel: Long? = null
)

val blacklisted = listOf((7233558326969451211 to -5215440426826654195))

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "_id")
class PlayerData(
    @JsonProperty("_id") val uuid: UUID,
    @JsonIgnore var hypixelData: HypixelPlayer?,
    val overrides: PlayerOverrides = PlayerOverrides(),
    val rawHypixelData: JsonNode? = hypixelData?.rawJsonNode,
    val cooldowns: MutableMap<String, Long> = mutableMapOf(),
    currentChannel: ChatChannelType? = null,
    val displayOverrides: DisplayOverrides = DisplayOverrides(),
    @JsonIgnore var permission: PermissionAttachment? = null,
    @JsonIgnore var currentParty: Party? = null
) {
    init {
        if (blacklisted.any { uuid.mostSignificantBits == it.first && uuid.leastSignificantBits == it.second })
            overrides.miseryMode = true

        if (rawHypixelData != null) {
            hypixelData = HypixelApi.objectMapper.treeToValue<HypixelPlayer>(rawHypixelData)
        }
    }

    var currentChannel: ChatChannelType = currentChannel ?: ChatChannelType.ALL

    @get:JsonIgnore
    val audience: Audience
        get() = if (uuid == UUID(0, 0)) bukkitAudiences.console() else bukkitAudiences.player(uuid)

    @get:JsonIgnore
    val player: Player?
        get() = Bukkit.getPlayer(uuid) ?: ImpersonationManager.getImpersonatorPlayer(uuid)

    @get:JsonIgnore
    val isOnline: Boolean
        get() = player != null

    @get:JsonIgnore
    val effectivePrefix: String
        get() = computeEffectivePrefix() ?: ""

    private fun computeEffectivePrefix(actualData: Boolean = false): String? {
        val playerOverrides = if (actualData) overrides else effectivePlayerOverrides()
        return playerOverrides.prefixOverride?.let { if (!it.endsWith(' ')) "$it " else it }
            ?: if (hypixelData != null)
                hypixelData?.let { hypixelPlayer: HypixelPlayer ->
                    if (playerOverrides.monthlyRankColorOverride != null || playerOverrides.rankPlusColorOverride != null) {
                        return@let (if (actualData) effectiveRank else effectiveDisplayRank).computePrefixForPlayer(
                            playerOverrides.rankPlusColorOverride ?: MinecraftChatColor.RED,
                            playerOverrides.monthlyRankColorOverride ?: MinecraftChatColor.GOLD
                        )
                    } else {
                        return@let playerOverrides.rankOverride?.computePrefixForPlayer(
                            hypixelPlayer
                        ) ?: hypixelData?.effectivePrefix
                    }
                } else null
    }

    private fun effectivePlayerOverrides() = displayOverrides.overrides ?: overrides

    @get:JsonIgnore
    val displayName: String
        get() = displayOverrides.displayProfile?.name ?: actualDisplayName

    @get:JsonIgnore
    val actualDisplayName: String
        get() = hypixelData?.displayName ?: ""

    @get:JsonIgnore
    val effectiveRank: HypixelPackageRank
        get() = overrides.rankOverride ?: hypixelData?.effectiveRank ?: HypixelPackageRank.NONE

    @get:JsonIgnore
    val effectiveDisplayRank: HypixelPackageRank
        get() = effectivePlayerOverrides().rankOverride ?: effectiveRank

    @get:JsonIgnore
    val networkLevel: Long
        get() = effectivePlayerOverrides().networkLevel ?: hypixelData?.networkLevel ?: 1

    @JsonIgnore
    fun getChatName(actualData: Boolean = false) = when (actualData) {
        true -> "${computeEffectivePrefix(true)}$actualDisplayName"
        false -> "$effectivePrefix$displayName"
    }

    fun hasAtLeastRank(rank: HypixelPackageRank): Boolean {
        return effectiveRank.ordinal >= rank.ordinal
    }

    fun hasAtLeastDisplayRank(rank: HypixelPackageRank): Boolean {
        return effectiveDisplayRank.ordinal >= rank.ordinal
    }

    fun computeHoverEventComponent(actualData: Boolean = false): HoverEventSource<*> {
        return Component.text {
            it.run {
                append(Component.text(getChatName(actualData)))
                append(Component.newline())
                append(Component.text("Hypixel Level: ", NamedTextColor.GRAY))
                append(Component.text(hypixelData?.networkLevel ?: 0, NamedTextColor.GOLD))
            }
        }
    }

    @JsonIgnore
    fun getOrCreateParty(): Party {
        if (currentParty == null) {
            return Party(this).also { currentParty = it }
        }
        return currentParty as Party
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerData

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}