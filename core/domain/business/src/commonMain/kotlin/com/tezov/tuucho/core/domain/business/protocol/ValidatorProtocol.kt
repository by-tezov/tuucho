package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain

interface ValidatorProtocol<T : Any> {

    fun updateValidity(value: T)

    val isValid: Boolean
}

interface FormValidatorProtocol<T : Any> : ValidatorProtocol<T> {

    fun getErrorMessage(language: LanguageModelDomain): String
}