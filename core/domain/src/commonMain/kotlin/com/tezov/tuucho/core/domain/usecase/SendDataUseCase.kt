package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class SendDataUseCase(
    private val repository: SendDataAndRetrieveMaterialRepositoryProtocol,
) {

    suspend fun invoke(url: String, data: JsonObject): JsonElement? = repository.process(url, data)

}