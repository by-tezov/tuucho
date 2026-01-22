package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ImageExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

@OpenForTest
class ProcessImageUseCase(
    private val imageExecutor: ImageExecutorProtocol,
) : UseCaseProtocol.Async<Input, Flow<ImageRepositoryProtocol.Image<*>>> {
    sealed class Input {
        data class Image(
            val image: ImageModelDomain,
            val imageObjectOriginal: JsonObject? = null,
        ) : Input()

        data class ImageObject(
            val imageObject: JsonObject,
        ) : Input()
    }

    override suspend fun invoke(
        input: Input
    ) = imageExecutor.process(input = input)
}
