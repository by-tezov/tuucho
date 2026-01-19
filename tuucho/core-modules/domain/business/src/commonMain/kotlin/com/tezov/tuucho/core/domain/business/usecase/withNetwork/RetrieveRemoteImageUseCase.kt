package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveRemoteImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveRemoteImageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class RetrieveRemoteImageUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imagesRepository: ImageRepositoryProtocol.Remote,
) : UseCaseProtocol.Async<Input, Output> {
    data class Input(
        val url: String,
    )

    data class Output(
        val image: ImageRepositoryProtocol.Image<*>,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.useCase.await {
            Output(
                image = imagesRepository.process<Any>(url)
            )
        }
    }
}
