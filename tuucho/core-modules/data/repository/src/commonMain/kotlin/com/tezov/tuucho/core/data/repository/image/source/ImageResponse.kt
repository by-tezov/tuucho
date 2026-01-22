package com.tezov.tuucho.core.data.repository.image.source

import coil3.Image

internal sealed class ImageResponse {
    abstract val target: String
    abstract val image: Image

    data class Placeholder(
        override val target: String,
        override val image: Image
    ) : ImageResponse()

    data class Success(
        override val target: String,
        override val image: Image
    ) : ImageResponse()
}
