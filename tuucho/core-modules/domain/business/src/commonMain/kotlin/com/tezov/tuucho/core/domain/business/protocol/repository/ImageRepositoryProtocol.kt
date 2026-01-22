package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Output
import kotlinx.coroutines.flow.Flow

interface ImageRepositoryProtocol {
    interface Image<S : Any> {
        val source: S
        val size: Long
        val width: Int
        val height: Int
    }

    fun <S : Any> process(
        images: Input.ImageModels
    ): Flow<Output<S>>
}
