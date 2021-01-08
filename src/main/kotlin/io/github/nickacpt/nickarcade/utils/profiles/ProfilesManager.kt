package io.github.nickacpt.nickarcade.utils.profiles

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.nickarcade.utils.interop.logger
import java.io.File

object ProfilesManager {
    private val ignoredProfiles = listOf("NickAc", "FuzzyTurtle87", "Bwar_", "BasicBwar", "not_irrelevant")

    private val mapper = HypixelApi.objectMapper

    fun loadProfiles(directory: File) {
        directory.listFiles()?.forEach {
            loadProfile(it)
        }
        profiles = profiles.distinctBy { it.name }.toMutableList()
    }

    private fun loadProfile(file: File) {
        val result = kotlin.runCatching {
            mapper.readValue<List<DumpedProfile>>(file).also { dumpList ->
                if (dumpList.any { it.name.contains('!') }) throw Exception("SkyBlock dump should not be included!")
                // Skip self-dumper when loading the profiles
                profiles.addAll(dumpList.filter { !ignoredProfiles.contains(it.name) && !it.tabShownName.contains("[NPC]") })
            }
        }
        if (result.isFailure) {
            logger.warn("Unable to load dumped profiles from ${file.name}: ${result.exceptionOrNull()}")
        }
    }

    var profiles = mutableListOf<DumpedProfile>()

}