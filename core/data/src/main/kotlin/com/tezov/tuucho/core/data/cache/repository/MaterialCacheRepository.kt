package com.tezov.tuucho.core.data.cache.repository

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import com.tezov.tuucho.core.data.cache.entity.JsonKeyValueEntity
import com.tezov.tuucho.core.data.cache.parser.decoder.DecoderConfig
import com.tezov.tuucho.core.data.cache.parser.decoder.MaterialModelDomainDecoder
import com.tezov.tuucho.core.data.parser.encoder.EncoderConfig
import com.tezov.tuucho.core.data.parser.encoder.MaterialSchemaDataEncoder
import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain
import kotlinx.serialization.json.JsonObject

class MaterialCacheRepository(
    private val database: Database,
    private val materialSchemaDataEncoder: MaterialSchemaDataEncoder,
    private val materialModelDomainDecoder: MaterialModelDomainDecoder
) {

    suspend fun shouldRefresh(url: String, version: String): Boolean {
//        return database.versioning().countVersions(url).let { result ->
//            result.any { it.version != version } || result.isEmpty()
//        }
        return true
    }

    suspend fun refreshCache(
        config: EncoderConfig,
        materialElement: JsonObject
    ) {
        //TODO auto purge obsolete entry
//        println(config.url)
//        println(material)

        val parts = materialSchemaDataEncoder.encode(materialElement, config)




//        with(parts) {
//            jsonKeyValueEntities.forEach { entry ->
//                processIdValueEntity(entry)
//            }
//            jsonObjectEntities.forEach { entry ->
//                processJsonEntity(entry)
//            }
//            val primaryKey = rootJsonObjectEntity?.let { entry ->
//                processJsonEntity(entry)
//            }
////            VersioningEntity(
////                url = config.url,
////                version = config.version,
////                rootPrimaryKey = primaryKey,
////                isShared = config.isShared,
////            ).also {
//////                println(it)
////
////                database.versioning().insertOrUpdate(it)
////            }
//        }
    }

    private suspend fun processIdValueEntity(entry: JsonKeyValueEntity): Long {
//        println(entry)

        return 0
//        return database.jsonKeyValue().insertOrUpdate(entry)
    }

    private suspend fun processJsonEntity(entry: JsonEntity): Long {
//        println(entry)

//        val primaryKey = database.jsonObject().insertOrUpdate(entry)
//        entry.children?.forEach { child ->
//            processJsonEntity(child)
//        }
//        return primaryKey
        return 0
    }

    suspend fun retrieve(config: DecoderConfig): MaterialModelDomain {
        return materialModelDomainDecoder.decode(config) ?: throw TODO()
    }

}
