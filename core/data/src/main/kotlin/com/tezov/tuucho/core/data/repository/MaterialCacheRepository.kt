package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.jsonEntityObject
import com.tezov.tuucho.core.data.parser.assembler.ExtraDataAssembler
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import kotlinx.serialization.json.JsonObject

class MaterialCacheRepository(
    private val database: Database,
    private val materialBreaker: MaterialBreaker,
    private val materialAssembler: MaterialAssembler
) {

    suspend fun shouldRefresh(url: String, version: String): Boolean {
//TODO
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
        val parts = materialBreaker.process(materialElement, config)
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

    suspend fun retrieve(config: ExtraDataAssembler): JsonObject? {
        return materialAssembler.process(config)
    }

}
