package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ImageExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class ProcessImageUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageExecutor: ImageExecutorProtocol,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val image: ImageModelDomain,
    )

    sealed class Output {
        class Element(
            val image: ImageRepositoryProtocol.Image<*, *>
        ) : Output()

        class ElementArray(
            val values: List<ImageRepositoryProtocol.Image<*, *>>,
        ) : Output()
    }

    override suspend fun invoke(
        input: Input
    ) = coroutineScopes.useCase.await {
        imageExecutor.process(input = input)
    }
}
