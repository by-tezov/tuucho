package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.SendDataMaterialRepositoryProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

class SendDataUseCase(
    private val coroutineDispatchers: CoroutineContextProviderProtocol,
    private val repository: SendDataMaterialRepositoryProtocol,
) {

    suspend fun invoke(url: String, data: JsonElement): JsonElement? =
        withContext(coroutineDispatchers.io) {
            repository.sendData(url, data)
        }

}