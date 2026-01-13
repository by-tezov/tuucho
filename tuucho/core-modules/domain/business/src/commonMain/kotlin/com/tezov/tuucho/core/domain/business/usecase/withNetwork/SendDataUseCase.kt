package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
class SendDataUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val sendDataAndRetrieveMaterialRepository: MaterialRepositoryProtocol.SendDataAndRetrieve,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val sendDataMiddlewares: List<SendDataMiddleware>
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
    ) = coroutineScopes.useCase.await {
        middlewareExecutor.process(
            middlewares = sendDataMiddlewares + terminalMiddleware(),
            context = SendDataMiddleware.Context(
                input = input,
            )
        )
    }

    private fun terminalMiddleware(): SendDataMiddleware = SendDataMiddleware { context, _ ->
        with(context.input) {
            Output(
                jsonObject = sendDataAndRetrieveMaterialRepository.process(url, jsonObject)
            )
        }
    }
}
