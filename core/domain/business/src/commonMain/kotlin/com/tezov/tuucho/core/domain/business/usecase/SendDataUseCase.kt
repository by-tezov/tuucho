package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class SendDataUseCase(
    private val sendDataAndRetrieveMaterialRepository: SendDataAndRetrieveMaterialRepositoryProtocol,
) {

    suspend fun invoke(url: String, dataObject: JsonObject): JsonElement? =
        sendDataAndRetrieveMaterialRepository.process(url, dataObject)

}