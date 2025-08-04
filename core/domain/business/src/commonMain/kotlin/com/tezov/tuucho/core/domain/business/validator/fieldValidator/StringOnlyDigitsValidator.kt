package com.tezov.tuucho.core.domain.business.validator.fieldValidator

import com.tezov.tuucho.core.domain.business.config.Language
import com.tezov.tuucho.core.domain.business.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonObject

class StringOnlyDigitsValidator(
    private val errorMessages: JsonObject,
) : FieldValidatorProtocol<String> {

    private var isValid = false

    override fun updateValidity(value: String) {
        isValid = value.isEmpty() || value.all { it.isDigit() }
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: Language): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[Language.Default.code].stringOrNull
            ?: ""
    }
}