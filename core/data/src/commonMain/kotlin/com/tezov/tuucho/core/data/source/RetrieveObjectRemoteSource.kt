package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.async
import kotlinx.serialization.json.JsonObject

class RetrieveObjectRemoteSource(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
) {

    suspend fun process(url: String): JsonObject = coroutineScopeProvider.network.async {
        materialNetworkSource.retrieve(url)
    }.await()

}