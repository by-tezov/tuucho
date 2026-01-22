package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import kotlinx.coroutines.flow.Flow

interface ImageMiddleware : MiddlewareProtocol<ImageMiddleware.Context, Flow<ImageRepositoryProtocol.Image<*>>> {
    data class Context(
        val input: RetrieveImageUseCase.Input.ImageModels
    )

    object Priority {
        const val LOW = 0
        const val DEFAULT = 100
        const val HIGH = 200
    }

    val priority: Int

    fun accept(
        image: ImageModel
    ): Boolean
}
