package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject

class StringMaxLengthFieldFormValidator(
    errorMessages: JsonObject,
    private val length: Int,
) : AbstractFormValidator<String>(errorMessages) {
    override fun updateValidity(
        value: String
    ) {
        isValid = value.length <= length
    }
}
