package com.tezov.tuucho.core.domain.validator.fieldValidator

import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import kotlinx.serialization.json.JsonObject

class StringEmailValidator(
    private val errorMessages: JsonObject,
) : FieldValidatorProtocol<String> {

    private var isValid = false

    override fun updateValidity(value: String) {
        isValid = value.isEmpty() || value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: Language): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[Language.Default.code].stringOrNull
            ?: ""
    }
}