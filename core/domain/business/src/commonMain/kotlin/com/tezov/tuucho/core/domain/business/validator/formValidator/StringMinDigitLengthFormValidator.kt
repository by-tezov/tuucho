package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject

class StringMinDigitLengthFormValidator(
    errorMessages: JsonObject,
    private val length: Int,
) : AbstractFormValidator<String>(errorMessages) {

    override fun updateValidity(value: String) {
        val digitCount = value.count { it.isDigit() }
        isValid = digitCount >= length
    }
}