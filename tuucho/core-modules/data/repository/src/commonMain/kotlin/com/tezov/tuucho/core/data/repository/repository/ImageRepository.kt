package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input

internal class ImageRepository(
    private val imageSource: ImageSource
) : ImageRepositoryProtocol {
    override fun <S : Any> process(input: Input) = imageSource.process<S>(input)
}
