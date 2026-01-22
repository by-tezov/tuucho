package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import kotlinx.coroutines.flow.Flow
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image as DomainImage

internal class ImageLocalRepository(
    private val imageSource: ImageSource
) : ImageRepositoryProtocol.Local {
    override fun <S : Any> process(
        target: String
    ): Flow<DomainImage<S>> = imageSource.processLocal(target)
}
