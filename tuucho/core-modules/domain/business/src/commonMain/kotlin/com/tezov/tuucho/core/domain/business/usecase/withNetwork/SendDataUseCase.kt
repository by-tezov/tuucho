package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.JsonObject

@OpenForTest
class SendDataUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val sendDataAndRetrieveMaterialRepository: MaterialRepositoryProtocol.SendDataAndRetrieve,
    private val middlewareExecutor: MiddlewareExecutorProtocolWithReturn,
    private val sendDataMiddlewares: List<SendDataMiddleware>
) : UseCaseProtocol.Async<Input, Output> {
    data class Input(
        val url: String,
        val jsonObject: JsonObject,
    )

    data class Output(
        val jsonObject: JsonObject?,
    )

    override suspend fun invoke(
        input: Input
    ) = coroutineScopes.io.withContext {
        middlewareExecutor
            .process(
                middlewares = sendDataMiddlewares + terminalMiddleware(),
                context = SendDataMiddleware.Context(
                    input = input,
                )
            ).flowOn(coroutineScopes.io.dispatcher)
            .firstOrNull()
    }

    private fun terminalMiddleware() = SendDataMiddleware { context, _ ->
        with(context.input) {
            val result = sendDataAndRetrieveMaterialRepository.process(url, jsonObject)
            send(Output(jsonObject = result))
        }
    }
}
