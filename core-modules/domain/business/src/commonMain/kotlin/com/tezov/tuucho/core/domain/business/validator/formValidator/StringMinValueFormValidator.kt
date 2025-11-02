package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject

class StringMinValueFormValidator(
    errorMessages: JsonObject,
    private val minValue: Int,
) : AbstractFormValidator<String>(errorMessages) {
    override fun updateValidity(
        value: String
    ) {
        isValid = value.isEmpty() || value.toIntOrNull()?.let { it > minValue } ?: false
    }
}
