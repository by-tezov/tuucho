package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSourceProtocol
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class SendDataAndRetrieveMaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSourceProtocol,
    private val materialRectifier: MaterialRectifierProtocol,
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