package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import kotlinx.coroutines.flow.Flow

interface ActionExecutorProtocol {
    suspend fun process(
        input: ProcessActionUseCase.Input
    ): Flow<Output>?
}
