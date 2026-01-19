package com.tezov.tuucho.core.data.repository.image.source

internal sealed class ImageRequest {
    data class Remote(
        val url: String,
    ) : ImageRequest()

    data class Local(
        val path: String,
    ) : ImageRequest()
}
