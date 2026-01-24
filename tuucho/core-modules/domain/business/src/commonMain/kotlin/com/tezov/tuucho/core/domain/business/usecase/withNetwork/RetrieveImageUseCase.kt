package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
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
) : UseCaseProtocol.Sync<Input, Flow<Output<S>>> {

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
                        ImageModel
                            .from(it, scope.tag)
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
        val tag: String?,
        val image: Image<S>
    )

    override fun invoke(input: Input) = imageRepository.process<S>(input = input)
}
