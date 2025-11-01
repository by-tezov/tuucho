package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class SendDataUseCase(
    private val sendDataAndRetrieveMaterialRepository: MaterialRepositoryProtocol.SendDataAndRetrieve,
) : UseCaseProtocol.Async<SendDataUseCase.Input, SendDataUseCase.Output> {
    data class Input(
        val url: String,
        val jsonObject: JsonObject,
    )

    data class Output(
        val jsonObject: JsonObject?,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        Output(
            jsonObject = sendDataAndRetrieveMaterialRepository.process(url, jsonObject)
        )
    }
}
