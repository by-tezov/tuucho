package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase.Output
import kotlinx.serialization.json.JsonObject

class SendDataUseCase(
    private val sendDataAndRetrieveMaterialRepository: SendDataAndRetrieveMaterialRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val url: String,
        val jsonObject: JsonObject,
    )

    data class Output(
        val jsonObject: JsonObject?,
    )

    override suspend fun invoke(input: Input) = with(input) {
        Output(
            jsonObject = sendDataAndRetrieveMaterialRepository.process(url, jsonObject)
        )
    }

}