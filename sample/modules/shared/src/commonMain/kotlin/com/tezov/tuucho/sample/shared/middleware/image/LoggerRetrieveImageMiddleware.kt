package com.tezov.tuucho.sample.shared.middleware.image

import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope

class LoggerRetrieveImageMiddleware(
    private val logger: Logger
) : RetrieveImageMiddleware<Any> {

    override suspend fun ProducerScope<RetrieveImageUseCase.Output<Any>>.process(
        context: RetrieveImageMiddleware.Context,
        next: MiddlewareProtocolWithReturn.Next<RetrieveImageMiddleware.Context, RetrieveImageUseCase.Output<Any>>?,
    ) {
        with(context.input) {
            logger.thread()
            logger.debug("IMAGE") {
                buildString {
                    appendLine("image to retrieve:")
                    models.forEach { appendLine(it) }
                }
            }
            next?.invoke(context)
        }
    }
}
