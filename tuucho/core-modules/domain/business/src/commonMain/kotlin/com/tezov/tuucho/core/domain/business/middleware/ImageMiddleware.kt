package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.model.ImageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase

interface ImageMiddleware : MiddlewareProtocol<ImageMiddleware.Context, ProcessImageUseCase.Output> {
    data class Context(
        val input: ProcessImageUseCase.Input.Image
    )

    object Priority {
        const val LOW = 0
        const val DEFAULT = 100
        const val HIGH = 200
    }

    val priority: Int

    fun accept(
        image: ImageModelDomain
    ): Boolean
}
