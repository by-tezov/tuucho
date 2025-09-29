package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonObject

abstract class AbstractFormValidator<T : Any>(
    private val errorMessages: JsonObject,
) : FormValidatorProtocol<T> {

    override var isValid: Boolean = false
        protected set

    override fun getErrorMessage(language: LanguageModelDomain): String {
        return errorMessages[language.code].stringOrNull
            ?: errorMessages[LanguageModelDomain.Default.code].stringOrNull
            ?: ""
    }
}