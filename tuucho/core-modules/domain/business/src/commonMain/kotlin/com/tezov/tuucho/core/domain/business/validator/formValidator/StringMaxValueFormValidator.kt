package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonObject

class StringMaxValueFormValidator(
    errorMessagesId: String?,
    private val maxValue: Int,
) : AbstractFormValidator<String>(errorMessagesId) {
    class Factory : FormValidatorProtocol.Factory {
        override val type: String = FormValidatorSchema.Value.Type.stringMaxValue

        override fun create(
            errorMessagesId: String?,
            prototypeObject: JsonObject
        ) = StringMaxValueFormValidator(
            errorMessagesId = errorMessagesId,
            maxValue = prototypeObject[FormValidatorSchema.Key.Param.value].string.toInt(),
        )
    }

    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.toIntOrNull()?.let { it < maxValue } ?: false
    }
}
