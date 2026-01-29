package com.tezov.tuucho.core.data.repository.image

data class ImageRequest(
    val command: String,
    val target: String,
    val cacheKey: String,
    val tags: Set<String>?,
    val tagsExcluder: Set<String>?
)
