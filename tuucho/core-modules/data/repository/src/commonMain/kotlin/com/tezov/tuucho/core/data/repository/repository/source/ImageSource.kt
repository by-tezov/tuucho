package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.image.ImageSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image as DomainImage

internal class ImageSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageSource: ImageSourceProtocol
) {
    fun <S : Any> processRemote(
        target: String
    ): Flow<DomainImage<S>> {
        @Suppress("UNCHECKED_CAST")
        return (imageSource.retrieveRemote(target) as Flow<DomainImage<S>>)
            .flowOn(coroutineScopes.image.context)
    }

    fun <S : Any> processLocal(
        target: String
    ): Flow<DomainImage<S>> {
        @Suppress("UNCHECKED_CAST")
        return (imageSource.retrieveLocal(target) as Flow<DomainImage<S>>)
            .flowOn(coroutineScopes.image.context)
    }
}
