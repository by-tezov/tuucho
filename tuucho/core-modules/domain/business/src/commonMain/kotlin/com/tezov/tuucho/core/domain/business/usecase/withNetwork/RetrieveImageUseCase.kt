package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@OpenForTest
class RetrieveImageUseCase<S : Any>(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageRepository: ImageRepositoryProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocolWithReturn,
    private val retrieveImageMiddlewares: List<RetrieveImageMiddleware<S>>,
) : UseCaseProtocol.Async<Input, Flow<Output<S>>> {
    data class Input(
        val models: List<ImageModel>,
    )

    data class Output<S : Any>(
        val image: Image<S>
    )

    override suspend fun invoke(
        input: Input
    ): Flow<Output<S>> = coroutineScopes.io.withContext {
        middlewareExecutor
            .process(
                middlewares = retrieveImageMiddlewares + terminalMiddleware(),
                context = RetrieveImageMiddleware.Context(
                    input = input,
                )
            ).flowOn(coroutineScopes.io.dispatcher)
    }

    private fun terminalMiddleware() = RetrieveImageMiddleware { context, _ ->
        with(context.input) {
            val result = imageRepository
                .process<S>(models = models)
                .map { Output(image = it) }
            result.collect { send(it) }
        }
    }
}
