package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.network.HttpNetworkSource
import com.tezov.tuucho.core.data.repository.network.HttpRequest
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class RemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val httpNetworkSource: HttpNetworkSource,
) {
    suspend fun resource(
        url: String
    ): JsonObject = coroutineScopes.network.await {
        httpNetworkSource.getResource(HttpRequest(url)).jsonObject
            ?: throw Exception("failed to retrieve resource at url $url")
    }

    suspend fun send(
        url: String,
        jsonObject: JsonObject
    ): JsonObject? = coroutineScopes.network.await {
        httpNetworkSource.postSend(HttpRequest(url, jsonObject)).jsonObject
    }

    suspend fun healthCheck(
        url: String
    ): JsonObject {
        val response = coroutineScopes.network.await {
            httpNetworkSource.getHealth(HttpRequest(url)).jsonObject
                ?: throw Exception("failed to check health at url $url")
        }
        return response
    }
}
