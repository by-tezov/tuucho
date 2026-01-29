package com.tezov.tuucho.core.data.repository.image

import coil3.Image

data class ImageResponse(
    val target: String,
    val tags: Set<String>?,
    val tagsExcluder: Set<String>?,
    val image: Image
)
