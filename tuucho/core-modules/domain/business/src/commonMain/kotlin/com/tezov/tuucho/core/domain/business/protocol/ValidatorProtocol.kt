package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.serialization.json.JsonObject

interface ValidatorProtocol<T : Any> {
    fun updateValidity(
        value: T?
    )

    val isValid: Boolean
}

interface FormValidatorProtocol<T : Any> : ValidatorProtocol<T> {
    interface Factory {
        val type: String

        fun create(
            errorMessagesId: String?,
            prototypeObject: JsonObject
        ): FormValidatorProtocol<*>
    }

    val errorMessagesId: String?
}
