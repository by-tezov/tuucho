package com.tezov.tuucho.core.data.repository.image

import coil3.Image
import com.tezov.tuucho.core.data.repository.image.source.ImageLoaderSource
import com.tezov.tuucho.core.data.repository.image.source.ImageRequest
import com.tezov.tuucho.core.data.repository.image.source.ImageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol.Image as DomainImage

interface ImageSourceProtocol {
    fun retrieveRemote(
        target: String
    ): Flow<DomainImage<Image>>

    fun retrieveLocal(
        target: String
    ): Flow<DomainImage<Image>>
}

internal class ImageSource(
    private val imageLoaderSource: ImageLoaderSource
) : ImageSourceProtocol {
    private fun ImageResponse.toDomainImage() = object : DomainImage<Image> {
        override val source: Image = this@toDomainImage.image
        override val size: Long
            get() = source.size
        override val width: Int
            get() = source.width
        override val height: Int
            get() = source.width
    }

    override fun retrieveRemote(
        target: String
    ): Flow<DomainImage<Image>> {
        val response = imageLoaderSource.retrieve(ImageRequest.Remote(target))
        return response.map { it.toDomainImage() }
    }

    override fun retrieveLocal(
        target: String
    ): Flow<DomainImage<Image>> {
        val response = imageLoaderSource.retrieve(ImageRequest.Local(target))
        return response.map { it.toDomainImage() }
    }
}
