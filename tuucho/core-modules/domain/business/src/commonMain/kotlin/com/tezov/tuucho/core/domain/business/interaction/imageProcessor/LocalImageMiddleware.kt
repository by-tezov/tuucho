package com.tezov.tuucho.core.domain.business.interaction.imageProcessor

import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.model.image.LocalImage
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveLocalImageUseCase

internal class LocalImageMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val retrieveLocalImage: RetrieveLocalImageUseCase
) : ImageMiddleware {
    override val priority: Int
        get() = ImageMiddleware.Priority.DEFAULT

    override fun accept(
        image: ImageModelDomain,
    ) = image.command == LocalImage.command

    override suspend fun process(
        context: ImageMiddleware.Context,
        next: MiddlewareProtocol.Next<ImageMiddleware.Context, ProcessImageUseCase.Output>?
    ) = with(context.input) {
        useCaseExecutor
            .await(
                useCase = retrieveLocalImage,
                input = RetrieveLocalImageUseCase.Input(
                    url = context.input.image.target
                )
            )?.let {
                ProcessImageUseCase.Output.Element(image = it.image)
            } ?: next?.invoke(context)
    }
}
