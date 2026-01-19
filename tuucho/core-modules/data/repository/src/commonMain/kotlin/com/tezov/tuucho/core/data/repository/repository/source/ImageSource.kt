package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.image.ImageSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

internal class ImageSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageSource: ImageSourceProtocol
) {
    suspend fun <S : Any> processRemote(
        target: String
    ): ImageRepositoryProtocol.Image<S> {
        val response = coroutineScopes.image.await {
            imageSource.retrieveRemote(target)
        }
        @Suppress("UNCHECKED_CAST")
        return response as ImageRepositoryProtocol.Image<S>
    }

    suspend fun <S : Any> processLocal(
        target: String
    ): ImageRepositoryProtocol.Image<S> {
        val response = coroutineScopes.image.await {
            imageSource.retrieveLocal(target)
        }
        @Suppress("UNCHECKED_CAST")
        return response as ImageRepositoryProtocol.Image<S>
    }
}
