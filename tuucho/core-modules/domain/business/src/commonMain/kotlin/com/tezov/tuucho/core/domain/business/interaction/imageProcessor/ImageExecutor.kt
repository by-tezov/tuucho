package com.tezov.tuucho.core.domain.business.interaction.imageProcessor

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ImageExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase.Input

internal class ImageExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val middlewares: List<ImageMiddleware>
) : ImageExecutorProtocol {

    override suspend fun process(
        input: Input
    ) = with(input) {
        when (this) {
            is Input.ImageObject -> {
                imageObject
                    .withScope(ImageSchema::Scope)
                    .source
                    ?.let { source ->
                        process(
                            Input.Image(
                                image = ImageModelDomain.from(source),
                            )
                        )
                    }
            }

            is Input.Image -> {
                process(this)
            }
        }
    }

    private suspend fun process(
        input: Input.Image
    ) = with(input) {
        coroutineScopes.image.await {
            val middlewaresToExecute = middlewares
                .filter { it.accept(image) }
                .sortedByDescending { it.priority }
            middlewaresToExecute
                .takeIf { it.isNotEmpty() }
                ?.let {
                    middlewareExecutor.process(
                        middlewares = it,
                        context = ImageMiddleware.Context(
                            input = input,
                        )
                    )
                }
        }
    }
}
