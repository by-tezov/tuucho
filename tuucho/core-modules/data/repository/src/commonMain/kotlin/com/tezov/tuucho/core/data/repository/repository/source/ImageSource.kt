package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.image.ImageLoaderSource
import com.tezov.tuucho.core.data.repository.image.ImageRequest
import com.tezov.tuucho.core.data.repository.image.ImageResponse
import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import coil3.Image as CoilImage

internal class ImageSource(
    private val imageLoaderSource: ImageLoaderSource
) {
    private fun cacheKey(
        url: String,
        id: String
    ) = "$url+$id"

    suspend fun <S : Any> process(
        models: List<ImageModel>
    ): Flow<Image<S>> {
        val requests = models.map {
            ImageRequest(
                command = it.command,
                target = it.target,
                cacheKey = cacheKey(url = it.target, id = it.id),
                tags = it.tags,
                tagsExcluder = it.tagsExcluder
            )
        }
        return imageLoaderSource
            .retrieve(requests)
            .mapNotNull { it.toDomainImage() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <S : Any> ImageResponse.toDomainImage() = object : Image<CoilImage> {
        override val source: CoilImage = this@toDomainImage.image
        override val tags = this@toDomainImage.tags
        override val tagsExcluder = this@toDomainImage.tagsExcluder
        override val size: Long
            get() = source.size
        override val width: Int
            get() = source.width
        override val height: Int
            get() = source.width
    } as Image<S>
}
