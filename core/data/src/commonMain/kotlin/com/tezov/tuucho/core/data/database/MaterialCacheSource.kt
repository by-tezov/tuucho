package com.tezov.tuucho.core.data.database

import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.jsonEntityObjectTree
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.assembler._system.ArgumentAssembler
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.breaker._system.ArgumentBreaker
import kotlinx.serialization.json.JsonElement

class MaterialCacheSource(
    private val jsonObjectQueries: JsonObjectQueries,
    private val versioningQueries: VersioningQueries,
    private val materialBreaker: MaterialBreaker,
    private val materialAssembler: MaterialAssembler,
) {

    suspend fun shouldRefresh(/* TODO */): Boolean {
//TODO
//        return database.versioning().countVersions(url).let { result ->
//            result.any { it.version != version } || result.isEmpty()
//        }
        return true
    }

    // Keep suspend to enforce caller to use it in background thread
    suspend fun refreshCache(
        version: String,
        argumentAssembler: ArgumentAssembler,
        argumentBreaker: ArgumentBreaker,
        materialElement: JsonElement,
    ) {
        //TODO auto purge obsolete entry
        val parts = materialBreaker.process(materialElement, argumentBreaker)
        with(parts) {
            val rootPrimaryKey = rootJsonEntity?.let { root ->
                jsonObjectQueries
                    .insertOrUpdate(root.jsonEntityObjectTree.content)
            }
            VersioningEntity(
                url = argumentAssembler.url,
                version = version,
                rootPrimaryKey = rootPrimaryKey,
                isShared = argumentBreaker.isShared
            ).also { versioningQueries.insertOrUpdate(it) }
            rootJsonEntity?.let { root ->
                root.flatten()
                    .asSequence()
                    .filter { it !== root }
                    .map { it.content }
                    .forEach {
                        jsonObjectQueries.insertOrUpdate(it)
                    }
            }
            jsonElementTree
                .asSequence()
                .flatMap { it.flatten() }
                .map { it.content }
                .forEach { jsonObjectQueries.insertOrUpdate(it) }
        }
    }

    suspend fun retrieve(argument: ArgumentAssembler): JsonElement? {
        return materialAssembler.process(argument)
    }

}