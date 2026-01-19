package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageRemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

internal class ImageRepository(
    private val imageRemoteSource: ImageRemoteSource
): ImageRepositoryProtocol.Remote{

    override suspend fun <T: Any, C: Any> process(url: String): ImageRepositoryProtocol.Image<T, C> {
        return imageRemoteSource.process(url)
    }
}
