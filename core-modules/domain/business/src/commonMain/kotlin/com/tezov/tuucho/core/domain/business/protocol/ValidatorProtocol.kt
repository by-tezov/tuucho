package com.tezov.tuucho.core.domain.business.protocol

interface ValidatorProtocol<T : Any> {
    fun updateValidity(
        value: T?
    )

    val isValid: Boolean
}

interface FormValidatorProtocol<T : Any> : ValidatorProtocol<T> {
    val errorMessagesId: String?
}
