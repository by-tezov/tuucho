package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRemoteSource(
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String): JsonObject {
        val response = materialNetworkSource.retrieve(url)
        return materialRectifier.process(response)
    }

}