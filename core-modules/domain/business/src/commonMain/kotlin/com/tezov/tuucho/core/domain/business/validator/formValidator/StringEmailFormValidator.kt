package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject

class StringEmailFormValidator(
    errorMessages: JsonObject,
) : AbstractFormValidator<String>(errorMessages) {

    override fun updateValidity(value: String) {
        isValid = value.isEmpty() || value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
    }

}