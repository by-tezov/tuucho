package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

internal class SendDataAndRetrieveMaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val remoteSource: RemoteSource,
    private val responseRectifier: ResponseRectifier,
    private val responseAssembler: ResponseAssembler,
    private val materialDatabaseSource: MaterialDatabaseSource,
) {
    suspend fun process(
        url: String,
        dataObject: JsonObject
    ): JsonObject? {
        val response = remoteSource.send(url, dataObject)
        return response?.let {
            coroutineScopes.parser.await {
                val responseRectified = responseRectifier.process(
                    context = RectifierProtocol.Context(
                        url = url
                    ),
                    responseObject = it
                )
                responseAssembler
                    .process(
                        context = AssemblerProtocol.Context(
                            url = url,
                            findAllRefOrNullFetcher = { from, type ->
                                coroutineScopes.database.await {
                                    materialDatabaseSource.getAllCommonRefOrNull(from, url, type)
                                }
                            }
                        ),
                        responseObject = responseRectified
                    )
            }
        }
    }
}
