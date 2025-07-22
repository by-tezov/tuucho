package com.tezov.tuucho.core.domain.protocol

import com.tezov.tuucho.core.domain.config.Language

interface ValidatorProtocol<T:Any> {

    fun updateValidity(value: T)

    fun isValid(): Boolean
}

interface FieldValidatorProtocol<T:Any>: ValidatorProtocol<T> {

     fun getErrorMessage(language: Language): String
}