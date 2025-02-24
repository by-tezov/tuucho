package com.tezov.tuucho.core.data.parser.encoder

open class EncoderConfig(
    val url: String,
    val version: String,
    val isShared: Boolean,
) {
    open fun copy(
        url: String? = null,
        version: String? = null,
        isShared: Boolean? = null,
    ) = EncoderConfig(
        url = url ?: this.url,
        version = version ?: this.version,
        isShared = isShared ?: this.isShared
    )
}