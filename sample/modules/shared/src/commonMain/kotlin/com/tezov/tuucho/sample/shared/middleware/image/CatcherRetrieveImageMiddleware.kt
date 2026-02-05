package com.tezov.tuucho.sample.shared.middleware.image

import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.model.image.LocalImageDefinition
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import kotlinx.coroutines.channels.ProducerScope

class CatcherRetrieveImageMiddleware() : RetrieveImageMiddleware<Any> {

    override suspend fun ProducerScope<RetrieveImageUseCase.Output<Any>>.process(
        context: RetrieveImageMiddleware.Context,
        next: MiddlewareProtocolWithReturn.Next<RetrieveImageMiddleware.Context, RetrieveImageUseCase.Output<Any>>?,
    ) {
        try {
            next?.invoke(context)
        } catch (_: Throwable) {
            next?.invoke(
                context.copy(
                    input = RetrieveImageUseCase.Input.create(
                        model = ImageModel.from(
                            command = LocalImageDefinition.command,
                            target = "img/safe-image",
                            id = "sage-image"
                        )
                    )
                )
            )
        }
    }
}
