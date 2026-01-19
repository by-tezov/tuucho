package com.tezov.tuucho.core.data.repository.image

import coil3.Canvas
import coil3.Image
import com.tezov.tuucho.core.data.repository.image.source.ImageLoaderSource
import com.tezov.tuucho.core.data.repository.image.source.ImageRequest
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol

interface ImageSourceProtocol {
    suspend fun retrieveRemote(
        target: String
    ): ImageRepositoryProtocol.Image<Image, Canvas>

    suspend fun retrieveLocal(
        target: String
    ): ImageRepositoryProtocol.Image<Image, Canvas>
}

internal class ImageSource(
    private val imageLoaderSource: ImageLoaderSource
) : ImageSourceProtocol {
    override suspend fun retrieveRemote(
        target: String
    ): ImageRepositoryProtocol.Image<Image, Canvas> {
        val response = imageLoaderSource.retrieve(ImageRequest.Remote(target))
        return object : ImageRepositoryProtocol.Image<Image, Canvas> {
            override val source: Image = response.image
            override val size: Long
                get() = source.size
            override val width: Int
                get() = source.width
            override val height: Int
                get() = source.width

            override fun draw(
                canvas: Canvas
            ) {
                source.draw(canvas)
            }
        }
    }

    override suspend fun retrieveLocal(
        target: String
    ): ImageRepositoryProtocol.Image<Image, Canvas> {
        val response = imageLoaderSource.retrieve(ImageRequest.Local(target))
        return object : ImageRepositoryProtocol.Image<Image, Canvas> {
            override val source: Image = response.image
            override val size: Long
                get() = source.size
            override val width: Int
                get() = source.width
            override val height: Int
                get() = source.width

            override fun draw(
                canvas: Canvas
            ) {
                source.draw(canvas)
            }
        }
    }
}
