package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

interface ActionMiddlewareExecutorProtocol {
    suspend fun process(
        input: ProcessActionUseCase.Input,
    )
}
