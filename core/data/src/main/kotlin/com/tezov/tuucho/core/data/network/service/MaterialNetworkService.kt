package com.tezov.tuucho.core.data.network.service

import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.model.ConfigModelDomain
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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

    suspend fun retrieve(url: String): JsonObject {
        val response = materialNetworkHttpRequest.retrieve(url)
        val data = response.json ?: throw IOException("failed to retrieve the url")
        val materialElement = jsonConverter.decodeFromString(
            deserializer = MapSerializer(String.serializer(), JsonElement.serializer()),
            string = data
        )
        return materialRectifier.process(JsonObject(materialElement))
    }
}