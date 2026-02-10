package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.TransformImageJsonArrayToImageModelUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.TransformImageJsonArrayToImageModelUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@OpenForTest
class TransformImageJsonArrayToImageModelUseCase(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val resolveLanguageValue: ResolveLanguageValueUseCase,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val jsonArray: JsonArray,
    )

    data class Output(
        val models: List<ImageModel>?
    )

    override suspend fun invoke(
        input: Input
    ): Output {
        val models = input.jsonArray.mapNotNull { jsonElement ->
            val jsonObject = (jsonElement as? JsonObject) ?: throw DomainException.Default("expect JsonObject")
            val scope = jsonObject.withScope(ImageSchema::Scope)
            val source = useCaseExecutor
                .await(
                    useCase = resolveLanguageValue,
                    input = ResolveLanguageValueUseCase.Input(
                        resolvedKey = ImageSchema.Key.default,
                        jsonObject = jsonObject
                    )
                )?.value
            source?.let {
                ImageModel.from(
                    value = it,
                    id = scope.id
                        ?.withScope(IdSchema::Scope)
                        ?.value
                        ?: throw DomainException.Default("should not be possible"),
                    tags = scope.tags,
                    tagsExcluder = scope.tagsExcluder
                )
            }
        }
        return Output(models = models.takeIf { it.isNotEmpty() })
    }

}
