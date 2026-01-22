package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.image.ImageLoaderSource
import com.tezov.tuucho.core.data.repository.image.ImageRequest
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase.Output
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import coil3.Image as CoilImage

internal class ImageSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val imageLoaderSource: ImageLoaderSource
) {
    fun <S : Any> process(
        images: Input.ImageModels
    ): Flow<Output<S>> {
        val requests = images.imageModels.map {
            ImageRequest(
                command = it.command,
                target = it.target,
                tag = it.tag
            )
        }
        return imageLoaderSource
            .retrieve(requests)
            .mapNotNull {
                Output<S>(
                    tag = it.tag,
                    image = it.image.toDomainImage()
                )
            }.flowOn(coroutineScopes.image.context)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <S : Any> CoilImage.toDomainImage() = object : ImageRepositoryProtocol.Image<CoilImage> {
        override val source: CoilImage = this@toDomainImage
        override val size: Long
            get() = source.size
        override val width: Int
            get() = source.width
        override val height: Int
            get() = source.width
    } as ImageRepositoryProtocol.Image<S>
}
