package com.tezov.tuucho.core.data.parser.assembler

open class ExtraDataAssembler(
    val url: String
) {
    open fun copy(
        url: String? = null
    ) = ExtraDataAssembler(
        url = url ?: this.url
    )
}