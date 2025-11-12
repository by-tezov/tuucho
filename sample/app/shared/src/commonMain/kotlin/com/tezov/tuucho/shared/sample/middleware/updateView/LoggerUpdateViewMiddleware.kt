package com.tezov.tuucho.shared.sample.middleware.updateView

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerUpdateViewMiddleware(
    private val logger: Logger
) : UpdateViewMiddleware {

    override suspend fun process(
        context: UpdateViewMiddleware.Context,
        next: MiddlewareProtocol.Next<UpdateViewMiddleware.Context, Unit>,
    ){
        with(logger) {
            println(context.input.route.value)
            println("-- input --")
            println(context.input.jsonObject)
            next.invoke(context)
        }
    }
}
