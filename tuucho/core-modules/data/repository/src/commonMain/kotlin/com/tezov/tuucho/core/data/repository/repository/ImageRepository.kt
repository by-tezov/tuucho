package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Output
import kotlinx.coroutines.flow.Flow

internal class ImageRepository(
    private val imageSource: ImageSource
) : ImageRepositoryProtocol {
    override fun <S : Any> process(
        images: Input.ImageModels
    ): Flow<Output<S>> = imageSource.process(images)
}
