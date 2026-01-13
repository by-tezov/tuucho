package com.tezov.tuucho.core.presentation.ui.exception

sealed class UiException(
    throwable: Throwable
) : Exception(throwable) {
    constructor(message: String) : this(Exception(message))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))

    class Default(
        throwable: Throwable
    ) : UiException(throwable) {
        constructor(message: String) : this(Exception(message))
        constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
    }
}
