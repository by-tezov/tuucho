package com.tezov.tuucho.sample.shared.middleware.image

import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.model.image.LocalImageDefinition
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow

class CatcherRetrieveImageMiddleware() : RetrieveImageMiddleware<Any> {

    override suspend fun ProducerScope<Flow<RetrieveImageUseCase.Output<Any>>>.process(
        context: RetrieveImageMiddleware.Context,
        next: MiddlewareProtocolWithReturn.Next<RetrieveImageMiddleware.Context, Flow<RetrieveImageUseCase.Output<Any>>>?,
    ) {
        try {
            println("before call next who will throw")
            next.invoke(context)
            println("after call next")
        } catch (_: Throwable) {

            println("I catched you")

//            next.invoke(
//                context.copy(
//                    input = RetrieveImageUseCase.Input.create(
//                        model = ImageModel.from(
//                            command = LocalImageDefinition.command,
//                            target = "img/logo-koin",
//                            cacheKey = "img/logo-koin"
//                        )
//                    )
//                )
//            )
        }
    }
}
