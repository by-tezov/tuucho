package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.network.NetworkSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class RemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val networkSource: NetworkSourceProtocol,
) {
    suspend fun resource(
        url: String
    ): JsonObject = coroutineScopes.network.await {
        networkSource.resource(url)
    }

    suspend fun send(
        url: String,
        jsonObject: JsonObject
    ) = coroutineScopes.network.await {
        networkSource.send(url, jsonObject)
    }

    suspend fun healthCheck(
        url: String
    ): JsonObject {
        val response = coroutineScopes.network.await {
            networkSource.healthCheck(url)
        }
        return response
    }
}
