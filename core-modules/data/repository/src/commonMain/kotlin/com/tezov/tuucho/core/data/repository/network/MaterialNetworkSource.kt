package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@OpenForTest
class MaterialNetworkSource(
    private val networkHttpRequest: NetworkHttpRequest,
    private val jsonConverter: Json
) {

    suspend fun health(url: String): JsonObject {
        val response = networkHttpRequest.getHealth(url)
        val data = response.json ?: throw DataException.Default("failed to check health at url $url")
        val jsonElement = jsonConverter.decodeFromString(
            deserializer = JsonObject.Companion.serializer(),
            string = data
        )
        return jsonElement
    }

    suspend fun resource(url: String): JsonObject {
        val response = networkHttpRequest.getResource(url)
        val data = response.json ?: throw DataException.Default("failed to retrieve resource at url $url")
        val jsonElement = jsonConverter.decodeFromString(
            deserializer = JsonObject.Companion.serializer(),
            string = data
        )
        return jsonElement
    }

    suspend fun send(url: String, data: JsonObject): JsonObject? {
        val json = jsonConverter.encodeToString(
            serializer = JsonObject.Companion.serializer(),
            value = data
        )
        val response = networkHttpRequest.postSend(url, RemoteRequest(json))
        return response.json?.let {
            val jsonElement = jsonConverter.decodeFromString(
                deserializer = JsonObject.Companion.serializer(),
                string = it
            )
            return jsonElement
        }
    }
}