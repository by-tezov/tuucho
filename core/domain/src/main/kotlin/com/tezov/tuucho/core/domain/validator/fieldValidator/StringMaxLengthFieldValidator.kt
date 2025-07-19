package com.tezov.tuucho.core.domain.validator.fieldValidator

import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import kotlinx.serialization.json.JsonObject

class StringMaxLengthFieldValidator(
    private val length: Int,
    private val errorMessages: JsonObject,
) : FieldValidatorProtocol<String> {

    private var isValid = false

    override fun updateValidity(value: String) {
        isValid = value.length <= length
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: Language): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[Language.Default.code].stringOrNull
            ?: ""
    }
}