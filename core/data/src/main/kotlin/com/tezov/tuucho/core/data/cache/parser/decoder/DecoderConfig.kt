package com.tezov.tuucho.core.data.cache.parser.decoder

open class DecoderConfig(
    val url: String,
) {
    open fun copy(
        url: String? = null,
    ) = DecoderConfig(
        url = url ?: this.url,
    )
}
