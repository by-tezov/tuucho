package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialAssembler: MaterialAssembler
) {

    suspend fun process(url: String): JsonObject? {
        val entity = coroutineScopes.database.on {
            materialDatabaseSource.findRootOrNull(url)
        } ?: return null
        return coroutineScopes.parser.on {
            materialAssembler.process(
                materialObject = entity.jsonObject,
                findAllRefOrNullFetcher = { from, type ->
                    coroutineScopes.database.on {
                        materialDatabaseSource.findAllRefOrNull(from, url, null, type)
                    }
                }
            )
        }
    }


}