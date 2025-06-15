package com.tezov.tuucho.core.data.cache.repository

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.cache.entity.VersioningEntity
import com.tezov.tuucho.core.data.cache.parser.decoder.DecoderConfig
import com.tezov.tuucho.core.data.cache.parser.decoder.MaterialModelDomainDecoder
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.jsonEntityObject
import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain
import kotlinx.serialization.json.JsonObject

class MaterialCacheRepository(
    private val database: Database,
    private val materialBreaker: MaterialBreaker,
    private val materialModelDomainDecoder: MaterialModelDomainDecoder
) {

    suspend fun shouldRefresh(url: String, version: String): Boolean {
//        return database.versioning().countVersions(url).let { result ->
//            result.any { it.version != version } || result.isEmpty()
//        }
        return true
    }

    suspend fun refreshCache(
        config: ExtraDataBreaker,
        materialElement: JsonObject
    ) {
        //TODO auto purge obsolete entry
        val parts = materialBreaker.encode(materialElement, config)
        with(parts) {
            val rootPrimaryKey = rootJsonEntity?.let { root ->
                database.jsonEntity()
                    .insertOrUpdate(root.jsonEntityObject.content)
            }
            VersioningEntity(
                url = config.url,
                version = config.version,
                rootPrimaryKey = rootPrimaryKey,
                isShared = config.isShared,
            ).also { database.versioning().insertOrUpdate(it) }
            rootJsonEntity?.let { root ->
                root.flatten()
                    .asSequence()
                    .filter { it != root }
                    .map { it.content }
                    .forEach {
                        database.jsonEntity().insertOrUpdate(it)
                    }
            }
            jsonEntityElement
                .asSequence()
                .flatMap { it.flatten() }
                .map { it.content }
                .forEach { database.jsonEntity().insertOrUpdate(it) }
        }
    }

    suspend fun retrieve(config: DecoderConfig): MaterialModelDomain {
        return materialModelDomainDecoder.decode(config) ?: throw TODO()
    }

}
