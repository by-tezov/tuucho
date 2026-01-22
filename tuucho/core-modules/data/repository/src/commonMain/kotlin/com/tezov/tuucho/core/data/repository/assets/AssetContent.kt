package com.tezov.tuucho.core.data.repository.assets

import okio.Source

data class AssetContent(
    val source: Source,
    val contentType: String,
    val size: Long
)
