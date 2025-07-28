package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import kotlinx.serialization.json.JsonObject

class RetrieveObjectRemoteSource(
    private val materialNetworkSource: MaterialNetworkSource,
) {

    suspend fun process(url: String): JsonObject {
        return materialNetworkSource.retrieve(url)
    }

}