package com.tezov.tuucho.core.domain.business.validator.fieldValidator

import com.tezov.tuucho.core.domain.business._system.stringOrNull
import com.tezov.tuucho.core.domain.business.config.Language
import com.tezov.tuucho.core.domain.business.protocol.FieldValidatorProtocol
import kotlinx.serialization.json.JsonObject

class StringMinValueValidator(
    private val errorMessages: JsonObject,
    private val minValue: Int,
) : FieldValidatorProtocol<String> {

    private var isValid = false

    override fun updateValidity(value: String) {
        isValid = value.isEmpty() || value.toIntOrNull()?.let { it > minValue } ?: false
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: Language): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[Language.Default.code].stringOrNull
            ?: ""
    }
}