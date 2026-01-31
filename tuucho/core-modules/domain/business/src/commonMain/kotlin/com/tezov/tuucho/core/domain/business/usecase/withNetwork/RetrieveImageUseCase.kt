package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol.Companion.process
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonArray

@OpenForTest
class RetrieveImageUseCase<S : Any>(
    private val imageRepository: ImageRepositoryProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val retrieveImageMiddlewares: List<RetrieveImageMiddleware<S>>
) : UseCaseProtocol.Async<Input, Flow<Output<S>>> {
    data class Input(
        val models: List<ImageModel>,
    ) {
        companion object {
            fun create(
                imageArray: JsonArray
            ): Input {
                val list = imageArray.mapNotNull { imageObject ->
                    val scope = imageObject.withScope(ImageSchema::Scope)
                    scope.source?.let {
                        ImageModel.from(
                            value = it,
                            cacheKey = scope.cacheKey ?: throw DomainException.Default("should not be possible"),
                            tags = scope.tags,
                            tagsExcluder = scope.tagsExcluder
                        )
                    }
                }
                return Input(models = list)
            }

            fun create(
                model: ImageModel
            ) = Input(models = listOf(model))
        }
    }

    data class Output<S : Any>(
        val image: Image<S>
    )

    override suspend fun invoke(input: Input) = middlewareExecutor
        .process(
            middlewares = retrieveImageMiddlewares + terminalMiddleware(),
            context = RetrieveImageMiddleware.Context(
                input = input,
            )
        ).firstOrNull()

    private fun terminalMiddleware(): RetrieveImageMiddleware<S> = RetrieveImageMiddleware<S> { context, _ ->
        with(context.input) {
            send(
                imageRepository
                    .process<S>(models = models)
                    .map { Output(image = it) }
            )
        }
    }

}
