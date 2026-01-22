package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

interface ActionExecutorProtocol {
    suspend fun process(
        input: ProcessActionUseCase.Input.ActionModel
    ): ProcessActionUseCase.Output?
}
