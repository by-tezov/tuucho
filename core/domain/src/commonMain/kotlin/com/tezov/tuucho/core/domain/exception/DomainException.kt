package com.tezov.tuucho.core.domain.exception

sealed class DomainException(throwable: Throwable): Exception(throwable) {
    constructor(message: String) : this(Exception(message))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))

    class Default(throwable: Throwable): DomainException(throwable) {
        constructor(message: String) : this(Exception(message))
        constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
    }
}