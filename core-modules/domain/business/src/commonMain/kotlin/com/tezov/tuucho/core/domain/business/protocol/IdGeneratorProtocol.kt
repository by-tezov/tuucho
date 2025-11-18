package com.tezov.tuucho.core.domain.business.protocol

interface IdGeneratorProtocol<T, R> {
    fun generate(
        input: T
    ) = generate()

    fun generate(): R
}
