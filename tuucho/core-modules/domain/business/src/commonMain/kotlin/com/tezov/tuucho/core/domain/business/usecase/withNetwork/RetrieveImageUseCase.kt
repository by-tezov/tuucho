package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonArray

@OpenForTest
class RetrieveImageUseCase<S : Any>(
    private val imageRepository: ImageRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Flow<Output<S>>> {
    sealed class Input {
        data class ImageModels(
            val imageModels: List<com.tezov.tuucho.core.domain.business.model.image.ImageModel>,
        ) : Input()

        data class ImageArray(
            val imageArray: JsonArray,
        ) : Input()
    }

    data class Output<S : Any>(
        val tag: String?,
        val image: Image<S>
    )

    override suspend fun invoke(
        input: Input
    ): Flow<Output<S>>? {
        val processorInput = with(input) {
            when (this) {
                is Input.ImageModels -> {
                    this
                }

                is Input.ImageArray -> {
                    val list = imageArray.mapNotNull { imageObject ->
                        val scope = imageObject.withScope(ImageSchema::Scope)
                        scope.source?.let {
                            com.tezov.tuucho.core.domain.business.model.image.ImageModel
                                .from(it, scope.tag)
                        }
                    }
                    Input.ImageModels(imageModels = list)
                }
            }
        }
        return imageRepository.process(images = processorInput)
    }
}
