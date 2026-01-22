package com.tezov.tuucho.core.domain.business.interaction.imageProcessor

import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.model.image.RemoteImage
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveRemoteImageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

internal class RemoteImageMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val retrieveRemoteImage: RetrieveRemoteImageUseCase
) : ImageMiddleware {
    override val priority: Int
        get() = ImageMiddleware.Priority.DEFAULT

    override fun accept(
        image: ImageModelDomain,
    ) = image.command == RemoteImage.command

    override suspend fun process(
        context: ImageMiddleware.Context,
        next: MiddlewareProtocol.Next<ImageMiddleware.Context, Flow<ImageRepositoryProtocol.Image<*>>>?
    ) = with(context.input) {
        val currentResult = useCaseExecutor.await(
            useCase = retrieveRemoteImage,
            input = RetrieveRemoteImageUseCase.Input(
                url = context.input.image.target
            )
        )
        val nextResult = next?.invoke(context)
        mergeResult(currentResult, nextResult)
    }

    private fun mergeResult(
        currentResult: Flow<ImageRepositoryProtocol.Image<*>>?,
        nextResult: Flow<ImageRepositoryProtocol.Image<*>>?,
    ) = when {
        currentResult != null && nextResult != null -> {
            merge(currentResult, nextResult)
        }

        currentResult != null -> {
            currentResult
        }

        else -> {
            nextResult
        }
    }
}
