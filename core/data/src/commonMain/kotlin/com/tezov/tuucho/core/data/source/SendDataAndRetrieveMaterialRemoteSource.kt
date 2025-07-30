package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject

class SendDataAndRetrieveMaterialRemoteSource(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String, data: JsonObject): JsonObject? {
        val response = withContext(coroutineContextProvider.io) {
            materialNetworkSource.send(url, data)
        }
        return response?.let {
            withContext(coroutineContextProvider.default) {
                materialRectifier.process(it)
            }
        }
    }
}