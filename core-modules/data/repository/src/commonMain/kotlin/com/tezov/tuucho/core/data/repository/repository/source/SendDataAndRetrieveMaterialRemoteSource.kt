package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

internal class SendDataAndRetrieveMaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val networkJsonObject: NetworkJsonObject,
    private val responseRectifier: ResponseRectifier,
    private val responseAssembler: ResponseAssembler,
    private val materialDatabaseSource: MaterialDatabaseSource,
) {
    suspend fun process(
        url: String,
        dataObject: JsonObject
    ): JsonObject? {
        val response = coroutineScopes.network.await {
            networkJsonObject.send(url, dataObject)
        }
        return response?.let {
            coroutineScopes.parser.await {
                val responseRectified = responseRectifier.process(it)
                responseAssembler
                    .process(
                        responseObject = responseRectified,
                        findAllRefOrNullFetcher = { from, type ->
                            coroutineScopes.database.await {
                                materialDatabaseSource.getAllCommonRefOrNull(from, url, type)
                            }
                        }
                    )
            }
        }
    }
}
