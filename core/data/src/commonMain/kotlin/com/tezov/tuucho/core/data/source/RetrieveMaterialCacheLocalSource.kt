package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialCacheLocalSource(
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialAssembler: MaterialAssembler
) {

    suspend fun process(url: String): JsonObject? {
        val entity = materialDatabaseSource.findRootOrNull(url) ?: return null
        return materialAssembler.process(
            material = entity.jsonObject,
            findAllRefOrNullFetcher = { from, type ->
                materialDatabaseSource.findAllRefOrNull(from, url, type)
            }
        )
    }

}