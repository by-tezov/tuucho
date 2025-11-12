package com.tezov.tuucho.shared.sample.middleware.sendData

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerSendDataMiddleware(
    private val logger: Logger
) : SendDataMiddleware {

    override suspend fun process(
        context: SendDataMiddleware.Context,
        next: MiddlewareProtocol.Next<SendDataMiddleware.Context, SendDataUseCase.Output>,
    ): SendDataUseCase.Output {
        with(logger) {
            println(context.input.url)
            println("-- input --")
            println(context.input.jsonObject)
            val output = next.invoke(context)
            println("-- output --")
            println(output)
            return output
        }
    }
}
