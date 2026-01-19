package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

internal class ImageLocalRepository(
    private val imageSource: ImageSource
) : ImageRepositoryProtocol.Local {
    override suspend fun <T : Any, C : Any> process(
        target: String
    ): ImageRepositoryProtocol.Image<T, C> = imageSource.processLocal(target)
}
