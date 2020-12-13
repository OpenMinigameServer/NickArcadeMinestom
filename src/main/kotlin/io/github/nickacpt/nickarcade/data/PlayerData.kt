package io.github.nickacpt.nickarcade.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.nickarcade.chat.ChatType
import java.util.*

class PlayerData(
    @JsonProperty("_id") val uuid: UUID,
    @JsonIgnore var hypixelData: HypixelPlayer?,
    var rankOverride: HypixelPackageRank? = null,
    var prefixOverride: String? = null,
    val rawHypixelData: JsonNode? = hypixelData?.rawJsonNode,
    val cooldowns: MutableMap<String, Long> = mutableMapOf(),
    val currentChat: ChatType = ChatType.ALL
) {
    init {
        if (rawHypixelData != null) {
            hypixelData = HypixelApi.objectMapper.treeToValue<HypixelPlayer>(rawHypixelData)
        }
    }

    @get:JsonIgnore
    val effectivePrefix: String
        get() = prefixOverride ?: rankOverride?.prefix ?: hypixelData?.effectivePrefix ?: ""

    @get:JsonIgnore
    val displayName: String
        get() = hypixelData?.displayName ?: ""

    @get:JsonIgnore
    val effectiveRank: HypixelPackageRank
        get() = rankOverride ?: hypixelData?.effectiveRank ?: HypixelPackageRank.NONE

    fun formatChatMessage(message: String): String {
        return "${getChatName()}${effectiveRank.chatMessagePrefix}: $message"
    }

    @JsonIgnore
    fun getChatName() = "${effectivePrefix}$displayName"
    fun hasAtLeastRank(rank: HypixelPackageRank): Boolean {
        return effectiveRank.ordinal >= rank.ordinal
    }
}