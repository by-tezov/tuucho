package com.tezov.tuucho.core.data.network.service

import com.tezov.tuucho.core.data.network._system.JsonRequestBody
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.model.ConfigModelDomain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okio.IOException

class MaterialNetworkService(
    private val materialNetworkHttpRequest: MaterialNetworkHttpRequest,
    private val materialRectifier: MaterialRectifier,
    private val jsonConverter: Json
) {

    suspend fun retrieveConfig(url: String): ConfigModelDomain {
        val response = materialNetworkHttpRequest.retrieve(url)
        val data = response.json ?: throw IOException("failed to retrieve the config")
        return jsonConverter.decodeFromString(data)
    }

    suspend fun retrieve(url: String): JsonElement {
        val response = materialNetworkHttpRequest.retrieve(url)
        val data = response.json ?: throw IOException("failed to retrieve the url")
        val jsonElement = jsonConverter.decodeFromString(
            deserializer = JsonElement.serializer(),
            string = data
        )
        return materialRectifier.process(jsonElement)
    }

    suspend fun send(url: String, data: JsonElement): JsonElement? {
        val json = jsonConverter.encodeToString(
            serializer = JsonElement.serializer(),
            value = data
        )
        val response = materialNetworkHttpRequest.send(url, JsonRequestBody(json))
        return response?.json?.let {
            val jsonElement = jsonConverter.decodeFromString(
                deserializer = JsonElement.serializer(),
                string = it
            )
            return jsonElement
        }
    }
}