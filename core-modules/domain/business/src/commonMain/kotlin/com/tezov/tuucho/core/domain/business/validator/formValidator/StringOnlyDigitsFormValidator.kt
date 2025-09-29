package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject

class StringOnlyDigitsFormValidator(
    errorMessages: JsonObject,
) : AbstractFormValidator<String>(errorMessages) {

    override fun updateValidity(value: String) {
        isValid = value.isEmpty() || value.all { it.isDigit() }
    }
}