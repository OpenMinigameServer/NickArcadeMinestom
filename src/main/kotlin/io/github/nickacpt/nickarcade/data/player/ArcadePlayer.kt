package io.github.nickacpt.nickarcade.data.player

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
import io.github.nickacpt.nickarcade.data.DisplayOverrides
import io.github.nickacpt.nickarcade.data.impersonation.ImpersonationManager
import io.github.nickacpt.nickarcade.game.Game
import io.github.nickacpt.nickarcade.game.MiniGameManager
import io.github.nickacpt.nickarcade.party.PartyManager
import io.github.nickacpt.nickarcade.party.model.Party
import io.github.nickacpt.nickarcade.utils.debugsubjects.RedirectAudience
import io.github.nickacpt.nickarcade.utils.interop.getLastColors
import io.github.nickacpt.nickarcade.utils.minestomAudiences
import io.github.nickacpt.nickarcade.utils.separator
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import java.util.*

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "_id")
class ArcadePlayer(
    @JsonProperty("_id") uuid: UUID,
    @JsonIgnore var hypixelData: HypixelPlayer?,
    val overrides: PlayerOverrides = PlayerOverrides(),
    var rawHypixelData: JsonNode? = hypixelData?.rawJsonNode,
    val cooldowns: MutableMap<String, Long> = mutableMapOf(),
    currentChannel: ChatChannelType? = null,
    val displayOverrides: DisplayOverrides = DisplayOverrides(),
) : ArcadeSender(uuid) {
    @JsonIgnore
    val extraData = mutableMapOf<String, Any?>()

    @JsonIgnore
    operator fun <T> contains(dataTag: ExtraDataTag<T>): Boolean {
        return get(dataTag) != null
    }

    @JsonIgnore
    operator fun <T> get(dataTag: ExtraDataTag<T>): T? {
        return extraData[dataTag.tagName] as? T?
    }

    @JsonIgnore
    operator fun <T> set(dataTag: ExtraDataTag<T>, value: T) {
        if (value == null) {
            extraData.remove(dataTag.tagName)
            return
        }
        extraData[dataTag.tagName] = value
    }

    init {
        if (rawHypixelData != null) {
            hypixelData = HypixelApi.objectMapper.treeToValue<HypixelPlayer>(rawHypixelData!!)
        } else if (hypixelData != null) {
            rawHypixelData = HypixelApi.objectMapper.valueToTree(hypixelData)
        }
    }

    @JsonIgnore
    fun getCurrentGame(): Game? {
        return MiniGameManager.getCurrentGame(this)
    }

    @JsonIgnore
    fun getCurrentParty(showPrompt: Boolean = false): Party? {
        return PartyManager.getParty(this).also {
            if (it == null && showPrompt) {
                audience.sendMessage(separator {
                    append(text("You are not currently in a party.", NamedTextColor.RED))
                })
            }
        }
    }

    @JsonIgnore
    fun setCurrentParty(party: Party?) {
        return PartyManager.setPlayerParty(this, party)
    }

    override var currentChannel: ChatChannelType = currentChannel ?: ChatChannelType.ALL

    @JsonIgnore
    var forwardTarget: ArcadePlayer? = null

    @get:JsonIgnore
    override val audience: Audience
        get() = forwardTarget?.asRedirectAudience(actualDisplayName) ?: if (uuid == UUID(
                0,
                0
            )
        ) minestomAudiences.console() else minestomAudiences.player(uuid)

    @JsonIgnore
    fun asRedirectAudience(name: String): Audience {
        return RedirectAudience(audience, text("[$name] "))
    }

    @get:JsonIgnore
    val player: Player?
        get() = forwardTarget?.player ?: MinecraftServer.getConnectionManager().getPlayer(uuid)
        ?: ImpersonationManager.getImpersonatorPlayer(uuid)

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
    override val displayName: String
        get() = displayOverrides.displayProfile?.name ?: actualDisplayName

    override val commandSender: CommandSender
        get() = player!!

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
    fun getChatName(): String = getChatName(false)

    @JsonIgnore
    fun getChatName(actualData: Boolean): String = getChatName(actualData, false)

    @JsonIgnore
    override fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String {
        var name = displayName
        var prefix = effectivePrefix
        if (actualData) {
            name = actualDisplayName
            prefix = computeEffectivePrefix(true) ?: effectivePrefix
        }

        if (colourPrefixOnly) {
            prefix = getLastColors(prefix)
        }

        return "$prefix$name"
    }

    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean {
        return actualData && hasAtLeastRank(rank) || hasAtLeastDisplayRank(rank)
    }

    private fun hasAtLeastRank(rank: HypixelPackageRank): Boolean {
        return effectiveRank.ordinal >= rank.ordinal
    }

    fun hasAtLeastDisplayRank(rank: HypixelPackageRank): Boolean {
        return effectiveDisplayRank.ordinal >= rank.ordinal
    }

    fun computeHoverEventComponent(actualData: Boolean = false): HoverEventSource<*> {
        return text {
            it.run {
                append(text(getChatName(actualData, false)))
                append(newline())
                append(text("Hypixel Level: ", NamedTextColor.GRAY))
                append(text(if (actualData) hypixelData?.networkLevel ?: 1 else networkLevel, NamedTextColor.GOLD))
            }
        }
    }

    @JsonIgnore
    fun getOrCreateParty(): Party {
        if (getCurrentParty() == null) {
            return PartyManager.createParty(this)
        }
        return getCurrentParty() as Party
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArcadePlayer

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}