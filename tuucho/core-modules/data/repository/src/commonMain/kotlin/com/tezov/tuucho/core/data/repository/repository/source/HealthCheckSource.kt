package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.network.NetworkSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

internal class HealthCheckSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val networkSource: NetworkSourceProtocol
) {
    suspend fun process(
        url: String
    ): JsonObject {
        val response = coroutineScopes.network.await {
            networkSource.healthCheck(url)
        }
        return response
    }
}
