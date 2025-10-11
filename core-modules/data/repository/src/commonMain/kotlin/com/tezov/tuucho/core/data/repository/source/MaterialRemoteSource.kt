package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.repository.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class MaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String): JsonObject {
        val response = coroutineScopes.network.await {
            materialNetworkSource.resource(url)
        }
        return coroutineScopes.parser.await {
            materialRectifier.process(response)
        }
    }

}