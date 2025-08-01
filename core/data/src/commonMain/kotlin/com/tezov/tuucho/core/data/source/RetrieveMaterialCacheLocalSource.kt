package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.async
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialCacheLocalSource(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialAssembler: MaterialAssembler
) {

    suspend fun process(url: String): JsonObject? {
        val entity = coroutineScopeProvider.database.async {
            materialDatabaseSource.findRootOrNull(url)
        }.await() ?: return null
        return coroutineScopeProvider.parser.async {
            materialAssembler.process(
                materialObject = entity.jsonObject,
                findAllRefOrNullFetcher = { from, type ->
                    coroutineScopeProvider.database.async {
                        materialDatabaseSource.findAllRefOrNull(from, url, type)
                    }.await()
                }
            )
        }.await()
    }


}