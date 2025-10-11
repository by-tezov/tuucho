package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ServerHealthCheckProtocol
import kotlinx.serialization.json.JsonObject

class NetworkHealthCheckSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
): ServerHealthCheckProtocol {

    override suspend fun process(url: String): JsonObject {
        val response = coroutineScopes.network.await {
            materialNetworkSource.health(url)
        }
        return response
    }

}