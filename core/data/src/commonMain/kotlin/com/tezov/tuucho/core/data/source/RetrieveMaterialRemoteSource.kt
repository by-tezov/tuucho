package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.async
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRemoteSource(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
) {

    suspend fun process(url: String): JsonObject {
        val response = coroutineScopeProvider.network.async {
            materialNetworkSource.retrieve(url)
        }.await()
        return coroutineScopeProvider.parser.async {
            materialRectifier.process(response)
        }.await()
    }

}