package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

internal class MaterialRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val networkJsonObject: NetworkJsonObject,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String): JsonObject {
        val response = coroutineScopes.network.await {
            networkJsonObject.resource(url)
        }
        return coroutineScopes.parser.await {
            materialRectifier.process(response)
        }
    }

}