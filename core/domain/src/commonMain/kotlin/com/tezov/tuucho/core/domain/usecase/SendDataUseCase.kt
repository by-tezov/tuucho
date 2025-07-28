package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class SendDataUseCase(
    private val coroutineDispatchers: CoroutineContextProviderProtocol,
    private val repository: SendDataAndRetrieveMaterialRepositoryProtocol,
) {

    suspend fun invoke(url: String, data: JsonObject): JsonElement? =
        withContext(coroutineDispatchers.io) {
            repository.process(url, data)
        }

}