package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException

interface IdGeneratorProtocol<T, R> {
    fun generate(
        input: T
    ): R = throw DomainException.Default("not implemented")

    fun generate(): R
}
