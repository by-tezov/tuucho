package com.tezov.tuucho.sample.shared.middleware.image

import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.model.image.LocalImageDefinition
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.supervisorScope

class CatcherRetrieveImageMiddleware() : RetrieveImageMiddleware<Any> {

    override suspend fun ProducerScope<Flow<RetrieveImageUseCase.Output<Any>>>.process(
        context: RetrieveImageMiddleware.Context,
        next: MiddlewareProtocol.Next<RetrieveImageMiddleware.Context, Flow<RetrieveImageUseCase.Output<Any>>>?,
    ) {
        supervisorScope {
            try {
                println("|> A")
                next.invoke(context)
                println("|> B")
            } catch (_: Throwable) {
                println("|> C")
                next.invoke(
                    context.copy(
                        input = RetrieveImageUseCase.Input.create(
                            model = ImageModel.from(
                                command = LocalImageDefinition.command,
                                target = "img/logo-koin",
                                cacheKey = "img/logo-koin"
                            )
                        )
                    )
                )
                println("|> D")
            }
        }
    }
}
