package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialCacheLocalSource(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialAssembler: MaterialAssembler
) {

    suspend fun process(url: String): JsonObject? = withContext(coroutineContextProvider.default) {
        val entity = materialDatabaseSource.findRootOrNull(url) ?: return@withContext null
        return@withContext materialAssembler.process(
            material = entity.jsonObject,
            findAllRefOrNullFetcher = { from, type ->
                materialDatabaseSource.findAllRefOrNull(from, url, type)
            }
        )
    }

}