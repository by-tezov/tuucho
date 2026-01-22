package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.source.HttpNetworkSource
import com.tezov.tuucho.core.data.repository.network.source.HttpRequest
import kotlinx.serialization.json.JsonObject

interface NetworkSourceProtocol {
    suspend fun resource(
        url: String
    ): JsonObject

    suspend fun send(
        url: String,
        jsonObject: JsonObject
    ): JsonObject?

    suspend fun healthCheck(
        url: String
    ): JsonObject
}

internal class NetworkSource(
    private val httpNetworkSource: HttpNetworkSource
) : NetworkSourceProtocol {
    override suspend fun resource(
        url: String
    ): JsonObject {
        val response = httpNetworkSource.getResource(HttpRequest(url))
        return response.jsonObject ?: throw DataException.Default("failed to retrieve resource at url $url")
    }

    override suspend fun send(
        url: String,
        jsonObject: JsonObject
    ): JsonObject? {
        val response = httpNetworkSource.postSend(HttpRequest(url, jsonObject))
        return response.jsonObject
    }

    override suspend fun healthCheck(
        url: String
    ): JsonObject {
        val response = httpNetworkSource.getHealth(HttpRequest(url))
        return response.jsonObject ?: throw DataException.Default("failed to check health at url $url")
    }
}
