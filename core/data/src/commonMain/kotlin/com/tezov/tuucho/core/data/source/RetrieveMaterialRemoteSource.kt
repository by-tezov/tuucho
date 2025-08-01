package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String): JsonObject {
        val response = coroutineScopes.onNetwork {
            materialNetworkSource.retrieve(url)
        }
        return coroutineScopes.onParser {
            materialRectifier.process(response)
        }
    }

}