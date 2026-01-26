package com.tezov.tuucho.core.data.repository.image

import coil3.Image

data class ImageResponse(
    val target: String,
    val tag: String?,
    val image: Image
)
