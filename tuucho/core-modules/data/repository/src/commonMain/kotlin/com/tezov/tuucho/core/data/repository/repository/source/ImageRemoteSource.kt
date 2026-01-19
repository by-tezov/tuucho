package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.image.ImageSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

internal class ImageRemoteSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageSource: ImageSourceProtocol
) {
    suspend fun <T : Any, C : Any> process(
        url: String
    ): ImageRepositoryProtocol.Image<T, C> {
        val response = coroutineScopes.image.await {
            imageSource.retrieveRemote(url)
        }
        @Suppress("UNCHECKED_CAST")
        return response as ImageRepositoryProtocol.Image<T, C>
    }
}
