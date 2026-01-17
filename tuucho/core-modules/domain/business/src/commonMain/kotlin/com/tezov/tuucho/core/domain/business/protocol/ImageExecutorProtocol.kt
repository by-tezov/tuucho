package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase

interface ImageExecutorProtocol {
    suspend fun process(
        input: ProcessImageUseCase.Input
    ): ProcessImageUseCase.Output?
}
