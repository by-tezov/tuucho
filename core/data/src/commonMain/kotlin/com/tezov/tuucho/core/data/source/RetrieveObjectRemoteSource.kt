package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject

class RetrieveObjectRemoteSource(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
) {

    suspend fun process(url: String): JsonObject = withContext(coroutineContextProvider.io) {
        materialNetworkSource.retrieve(url)
    }

}