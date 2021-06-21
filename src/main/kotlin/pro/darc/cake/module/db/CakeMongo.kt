package pro.darc.cake.module.db

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pro.darc.cake.module.extensions.Config

val client: CoroutineClient by lazy {
    System.setProperty("org.litote.mongo.mapping.service", "org.litote.kmongo.serialization.SerializationClassMappingTypeService")
    KMongo.createClient(Config.db.getString("db uri")!!).coroutine
}
