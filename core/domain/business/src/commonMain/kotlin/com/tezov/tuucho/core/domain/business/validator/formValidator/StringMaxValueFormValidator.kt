package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonObject

class StringMaxValueFormValidator(
    private val errorMessages: JsonObject,
    private val maxValue: Int,
) : FormValidatorProtocol<String> {

    private var isValid = false

    override fun updateValidity(value: String) {
        isValid = value.isEmpty() || value.toIntOrNull()?.let { it < maxValue } ?: false
    }

    override fun isValid() = isValid

    override fun getErrorMessage(language: LanguageModelDomain): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[LanguageModelDomain.Default.code].stringOrNull
            ?: ""
    }
}