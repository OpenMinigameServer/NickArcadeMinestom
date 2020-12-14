package io.github.nickacpt.nickarcade.utils.profiles

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.nickarcade.utils.pluginInstance
import java.io.File

object ProfilesManager {
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
                profiles.addAll(dumpList.filterNot { it.name == "NickAc" && !it.tabShownName.contains("[NPC]") })
            }
        }
        if (result.isFailure) {
            pluginInstance.logger.warning("Unable to load dumped profiles from ${file.name}: ${result.exceptionOrNull()}")
        }
    }

    var profiles = mutableListOf<DumpedProfile>()

}