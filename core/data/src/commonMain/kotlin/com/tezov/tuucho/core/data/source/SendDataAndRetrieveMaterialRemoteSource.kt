package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class SendDataAndRetrieveMaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String, dataObject: JsonObject): JsonObject? {
        val response = coroutineScopes.network.await {
            materialNetworkSource.send(url, dataObject)
        }
        return response?.let {
            coroutineScopes.parser.await {
                materialRectifier.process(it)
            }
        }
    }
}