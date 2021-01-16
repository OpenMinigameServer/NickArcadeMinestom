package io.github.nickacpt.nickarcade.data.player

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.nickacpt.hypixelapi.models.HypixelPackageRank
import io.github.nickacpt.nickarcade.chat.ChatChannelType
import net.kyori.adventure.audience.Audience
import net.minestom.server.command.CommandSender
import java.util.*

abstract class ArcadeSender(@JsonProperty("_id") val uuid: UUID) {

    @get:JsonIgnore
    abstract val audience: Audience

    @get:JsonIgnore
    abstract val displayName: String

    @get:JsonIgnore
    abstract val commandSender: CommandSender

    abstract var currentChannel: ChatChannelType

    abstract fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean = false): Boolean

    @JsonIgnore
    abstract fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String
}