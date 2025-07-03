package com.tezov.tuucho.core.domain.validator.fieldValidator

import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import kotlinx.serialization.json.JsonObject

class StringMinDigitLengthFieldValidator(
    private val length: Int,
    private val errorMessages: JsonObject,
) : FieldValidatorProtocol<String> {

    private var isValid = true

    override fun updateValidity(value: String) {
        val digitCount = value.count { it.isDigit() }
        isValid = digitCount >= length
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: Language): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[Language.Default.code].stringOrNull
            ?: ""
    }
}