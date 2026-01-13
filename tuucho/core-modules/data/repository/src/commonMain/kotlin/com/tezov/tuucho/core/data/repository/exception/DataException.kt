package com.tezov.tuucho.core.data.repository.exception

sealed class DataException(
    throwable: Throwable
) : Exception(throwable) {
    constructor(message: String) : this(Exception(message))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))

    class Default(
        throwable: Throwable
    ) : DataException(throwable) {
        constructor(message: String) : this(Exception(message))
        constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
    }
}
