package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import kotlinx.coroutines.flow.Flow

interface ImageExecutorProtocol {
    suspend fun process(
        input: ProcessImageUseCase.Input
    ): Flow<ImageRepositoryProtocol.Image<*>>?
}
