package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject

class StringMaxValueFormValidator(
    errorMessages: JsonObject,
    private val maxValue: Int,
) : AbstractFormValidator<String>(errorMessages) {
    override fun updateValidity(
        value: String
    ) {
        isValid = value.isEmpty() || value.toIntOrNull()?.let { it < maxValue } ?: false
    }
}
