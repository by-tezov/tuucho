package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveLocalImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveLocalImageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class RetrieveLocalImageUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imagesRepository: ImageRepositoryProtocol.Local,
) : UseCaseProtocol.Async<Input, Output> {
    data class Input(
        val url: String,
    )

    data class Output(
        val image: ImageRepositoryProtocol.Image<*, *>,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.useCase.await {
            Output(
                image = imagesRepository.process<Any, Any>(url)
            )
        }
    }
}
