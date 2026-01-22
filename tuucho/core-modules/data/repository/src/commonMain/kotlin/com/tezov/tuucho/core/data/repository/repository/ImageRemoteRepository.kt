package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

internal class ImageRemoteRepository(
    private val imageSource: ImageSource
) : ImageRepositoryProtocol.Remote,
    ImageRepositoryProtocol.Local {
    override suspend fun <S : Any> process(
        target: String
    ): ImageRepositoryProtocol.Image<S> = imageSource.processRemote(target)
}
