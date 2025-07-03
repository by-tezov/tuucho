package com.tezov.tuucho.core.domain.validator

import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.protocol.ValidatorProtocol
import kotlinx.serialization.json.JsonObject

class StringMinLengthValidator(
    private val length: Int,
    private val errorMessages: JsonObject,
) : ValidatorProtocol<String> {

    private var isValid = true

    override fun updateValidity(value: String) {
        isValid = value.length >= length
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: Language): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[Language.Default.code].stringOrNull
            ?: ""
    }
}