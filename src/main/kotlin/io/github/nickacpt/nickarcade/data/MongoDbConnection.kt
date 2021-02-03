package io.github.nickacpt.nickarcade.data

import com.fasterxml.jackson.databind.Module
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.internal.connection.ServerAddressHelper
import io.github.nickacpt.hypixelapi.utis.HypixelApi
import io.github.nickacpt.nickarcade.data.helpers.ArcadeModule
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.util.KMongoConfiguration
import org.litote.kmongo.util.KMongoJacksonFeature

object MongoDbConnectionHelper {
    private fun registerModule(module: Module) {
        KMongoConfiguration.extendedJsonMapper.registerModule(module)
        KMongoConfiguration.registerBsonModule(module)
    }

    fun buildClient(host: String, port: Int, userName: String, database: String, password: String): CoroutineClient {
        KMongoJacksonFeature.setUUIDRepresentation(UuidRepresentation.STANDARD)
        HypixelApi.configureMapper(KMongoConfiguration.bsonMapper)
        HypixelApi.configureMapper(KMongoConfiguration.extendedJsonMapper)
        registerModule(ArcadeModule)

        return KMongo.createClient(
            MongoClientSettings.builder()
                .let {
                    if (userName.isNotEmpty() && password.isNotEmpty()) {
                        it.credential(MongoCredential.createCredential(userName, database, password.toCharArray()))
                    }
                    it
                }
                .applyToClusterSettings {
                    it.hosts(listOf(ServerAddressHelper.createServerAddress(host, port)))
                }
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build()
        ).coroutine
    }
}