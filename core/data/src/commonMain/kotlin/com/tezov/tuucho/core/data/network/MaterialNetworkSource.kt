package com.tezov.tuucho.core.data.network

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.network._system.JsonRequestBody
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class MaterialNetworkSource(
    private val materialNetworkHttpRequest: MaterialNetworkHttpRequest,
    private val materialRectifier: MaterialRectifier,
    private val jsonConverter: Json
) {

    suspend fun retrieveConfig(url: String): JsonObject {
        val response = materialNetworkHttpRequest.retrieve(url)
        val data = response.json ?: throw DataException.Default("failed to retrieve the config")
        return jsonConverter.decodeFromString(
            deserializer = JsonObject.Companion.serializer(),
            string = data
        )
    }

    suspend fun retrieve(url: String): JsonObject {
        val response = materialNetworkHttpRequest.retrieve(url)
        val data = response.json ?: throw DataException.Default("failed to retrieve the url")
        val jsonElement = jsonConverter.decodeFromString(
            deserializer = JsonObject.Companion.serializer(),
            string = data
        )
        return materialRectifier.process(jsonElement)
    }

    suspend fun send(url: String, data: JsonElement): JsonObject? {
        val json = jsonConverter.encodeToString(
            serializer = JsonElement.Companion.serializer(),
            value = data
        )
        val response = materialNetworkHttpRequest.send(url, JsonRequestBody(json))
        return response.json?.let {
            val jsonElement = jsonConverter.decodeFromString(
                deserializer = JsonObject.Companion.serializer(),
                string = it
            )
            return jsonElement
        }
    }
}