package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonObject

class StringMinDigitLengthFormValidator(
    errorMessagesId: String?,
    private val length: Int,
) : AbstractFormValidator<String>(errorMessagesId) {
    class Factory : FormValidatorProtocol.Factory {
        override val type: String = FormValidatorSchema.Value.Type.stringMinDigitLength

        override fun create(
            errorMessagesId: String?,
            prototypeObject: JsonObject
        ) = StringMinDigitLengthFormValidator(
            errorMessagesId = errorMessagesId,
            length = prototypeObject[FormValidatorSchema.Key.Param.length].string.toInt(),
        )
    }

    override fun updateValidity(
        value: String?
    ) {
        val digitCount = value?.count { it.isDigit() } ?: 0
        isValid = digitCount >= length
    }
}
