package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries.Companion.jsonObject
import com.tezov.tuucho.core.data.database.dao.VersioningQueries.Companion.versioning
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.jsonEntityObjectTree
import com.tezov.tuucho.core.data.parser.assembler.ExtraDataAssembler
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.breaker.ExtraDataBreaker
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import kotlinx.serialization.json.JsonElement

class MaterialCacheRepository(
    private val database: Database,
    private val materialBreaker: MaterialBreaker,
    private val materialAssembler: MaterialAssembler,
) {

    suspend fun shouldRefresh(url: String, version: String): Boolean {
//TODO
//        return database.versioning().countVersions(url).let { result ->
//            result.any { it.version != version } || result.isEmpty()
//        }
        return true
    }

    fun refreshCache(
        config: ExtraDataBreaker,
        materialElement: JsonElement,
    ) {
        //TODO auto purge obsolete entry
        val parts = materialBreaker.process(materialElement, config)
        with(parts) {
            val rootPrimaryKey = rootJsonEntity?.let { root ->
                database.jsonObject()
                    .insertOrUpdate(root.jsonEntityObjectTree.content)
            }
            VersioningEntity(
                url = config.url,
                version = config.version,
                rootPrimaryKey = rootPrimaryKey,
                isShared = config.isShared
            ).also { database.versioning().insertOrUpdate(it) }
            rootJsonEntity?.let { root ->
                root.flatten()
                    .asSequence()
                    .filter { it !== root }
                    .map { it.content }
                    .forEach {
                        database.jsonObject().insertOrUpdate(it)
                    }
            }
            jsonElementTree
                .asSequence()
                .flatMap { it.flatten() }
                .map { it.content }
                .forEach { database.jsonObject().insertOrUpdate(it) }
        }
    }

    suspend fun retrieve(config: ExtraDataAssembler): JsonElement? {
        return materialAssembler.process(config)
    }

}
