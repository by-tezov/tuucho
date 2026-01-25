package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol.Companion.process
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.JsonObject

@OpenForTest
class SendDataUseCase(
    private val sendDataAndRetrieveMaterialRepository: MaterialRepositoryProtocol.SendDataAndRetrieve,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
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
    ) = middlewareExecutor
        .process(
            middlewares = sendDataMiddlewares + terminalMiddleware(),
            context = SendDataMiddleware.Context(
                input = input,
            )
        ).firstOrNull()

    private fun terminalMiddleware(): SendDataMiddleware = SendDataMiddleware { context, _ ->
        with(context.input) {
            emit(
                Output(
                    jsonObject = sendDataAndRetrieveMaterialRepository.process(url, jsonObject)
                )
            )
        }
    }
}
