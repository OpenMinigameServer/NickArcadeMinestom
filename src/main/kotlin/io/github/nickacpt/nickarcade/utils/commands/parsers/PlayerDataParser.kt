package io.github.nickacpt.nickarcade.utils.commands.parsers

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.bukkit.parsers.PlayerArgument
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import io.github.nickacpt.hypixelapi.models.HypixelPlayer
import io.github.nickacpt.nickarcade.data.player.PlayerData
import io.github.nickacpt.nickarcade.data.player.PlayerDataManager
import io.github.nickacpt.nickarcade.data.player.getPlayerData
import io.github.nickacpt.nickarcade.utils.div
import io.github.nickacpt.nickarcade.utils.sync
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.litote.kmongo.eq
import org.litote.kmongo.include
import java.util.*

class PlayerDataParser<C> : ArgumentParser<C, PlayerData> {
    override fun parse(commandContext: CommandContext<C>, queue: Queue<String>): ArgumentParseResult<PlayerData> {
        return runBlocking {
            val argument: String? = queue.peek()
            if (argument != null) {
                queue.remove()

                //Try a name
                var data = Bukkit.getPlayer(argument)?.getPlayerData()

                //Try a UUID
                if (data == null) {
                    data = kotlin.runCatching { Bukkit.getPlayer(UUID.fromString(argument)) }.getOrNull()
                        ?.getPlayerData()
                }

                //Try finding from the displayName of a PlayerData
                if (data == null) {
                    val displayName = PlayerData::rawHypixelData / HypixelPlayer::displayName
                    val foundPlayer = PlayerDataManager.playerDataCollection.findOne(displayName eq argument)
                    if (foundPlayer != null) {
                        data = foundPlayer
                    }

                }
                if (data != null) {
                    if (PlayerDataManager.isPlayerDataLoaded(data.uuid)) {
                        //Use the player data we already have loaded
                        data = PlayerDataManager.getPlayerData(data.uuid, data.actualDisplayName)
                    } else {
                        //Store in memory first, then return.
                        PlayerDataManager.storeInMemory(data)
                    }

                    return@runBlocking ArgumentParseResult.success(data)
                }
                return@runBlocking ArgumentParseResult.failure(
                    PlayerArgument.PlayerParseException(
                        argument,
                        commandContext
                    )
                )
            }
            return@runBlocking ArgumentParseResult.failure<PlayerData>(
                NoInputProvidedException(
                    javaClass,
                    commandContext
                )
            )
        }
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): MutableList<String> {
        return runBlocking {
            val displayName = PlayerData::rawHypixelData / HypixelPlayer::displayName
            val allElements =
                PlayerDataManager.playerDataCollection.find()
                    .projection(include(PlayerData::uuid, displayName))
                    .toList()

            return@runBlocking (allElements.map { it.displayName } + sync { Bukkit.getOnlinePlayers() }.map { it.name }).distinct()
                .toMutableList()
        }
    }

}