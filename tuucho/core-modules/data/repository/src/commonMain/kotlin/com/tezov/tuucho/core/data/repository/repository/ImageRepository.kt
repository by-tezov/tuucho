package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

internal class ImageRepository(
    private val imageSource: ImageSource
) : ImageRepositoryProtocol {
    override fun <S : Any> process(
        models: List<ImageModel>
    ) = imageSource.process<S>(models)
}
