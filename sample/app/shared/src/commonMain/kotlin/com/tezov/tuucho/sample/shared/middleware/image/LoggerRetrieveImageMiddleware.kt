package com.tezov.tuucho.sample.shared.middleware.image

import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

class LoggerRetrieveImageMiddleware(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : RetrieveImageMiddleware<Any> {

    override suspend fun ProducerScope<Flow<RetrieveImageUseCase.Output<Any>>>.process(
        context: RetrieveImageMiddleware.Context,
        next: MiddlewareProtocol.Next<RetrieveImageMiddleware.Context, Flow<RetrieveImageUseCase.Output<Any>>>?,
    ) {
        with(context.input) {
            logger.debug("THREAD") { systemInformation.currentThreadName() }
            logger.debug("IMAGE") {
                buildString {
                    appendLine("image to retrieve:")
                    models.forEach { appendLine(it) }
                }
            }
            next.invoke(context)
        }
    }
}
