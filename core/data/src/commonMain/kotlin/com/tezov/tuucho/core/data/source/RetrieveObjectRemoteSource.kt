package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveObjectRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
) {

    suspend fun process(url: String): JsonObject = coroutineScopes.onNetwork {
        materialNetworkSource.retrieve(url)
    }

}