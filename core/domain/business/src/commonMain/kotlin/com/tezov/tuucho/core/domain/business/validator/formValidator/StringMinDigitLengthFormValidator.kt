package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonObject

class StringMinDigitLengthFormValidator(
    private val length: Int,
    private val errorMessages: JsonObject,
) : FormValidatorProtocol<String> {

    private var isValid = false

    override fun updateValidity(value: String) {
        val digitCount = value.count { it.isDigit() }
        isValid = digitCount >= length
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: LanguageModelDomain): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[LanguageModelDomain.Default.code].stringOrNull
            ?: ""
    }
}