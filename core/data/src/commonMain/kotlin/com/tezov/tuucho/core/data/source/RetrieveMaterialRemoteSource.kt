package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRemoteSource(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String): JsonObject {
        val response = withContext(coroutineContextProvider.io) {
            materialNetworkSource.retrieve(url)
        }
        return withContext(coroutineContextProvider.default) {
            materialRectifier.process(response)
        }
    }

}