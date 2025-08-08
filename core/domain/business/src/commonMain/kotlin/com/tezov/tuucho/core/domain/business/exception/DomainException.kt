package com.tezov.tuucho.core.domain.business.exception

sealed class DomainException(throwable: Throwable) : Exception(throwable) {
    constructor(message: String) : this(Exception(message))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))

    class Unknown(throwable: Throwable) : DomainException(throwable)

    class Default(throwable: Throwable) : DomainException(throwable) {
        constructor(message: String) : this(Exception(message))
        constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
    }
}