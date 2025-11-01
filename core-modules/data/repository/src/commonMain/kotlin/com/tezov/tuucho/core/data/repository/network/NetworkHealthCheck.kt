package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.source.NetworkHttpRequestSource
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ServerHealthCheckProtocol
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class NetworkHealthCheck(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val networkHttpRequestSource: NetworkHttpRequestSource,
    private val jsonConverter: Json
): ServerHealthCheckProtocol {

    override suspend fun process(url: String): JsonObject {
        val jsonElement = coroutineScopes.network.await {
            val response = networkHttpRequestSource.getHealth(url)
            val data = response.json ?: throw DataException.Default("failed to check health at url $url")
            jsonConverter.decodeFromString(
                deserializer = JsonObject.Companion.serializer(),
                string = data
            )
        }
        return jsonElement
    }
}