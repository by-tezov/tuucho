package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

internal class RetrieveObjectRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val networkJsonObject: NetworkJsonObject,
) {
    suspend fun process(
        url: String
    ): JsonObject = coroutineScopes.network.await {
        networkJsonObject.resource(url)
    }
}
