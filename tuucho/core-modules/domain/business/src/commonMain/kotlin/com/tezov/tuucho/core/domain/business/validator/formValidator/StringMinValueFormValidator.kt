package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonObject

class StringMinValueFormValidator(
    errorMessagesId: String?,
    private val minValue: Int,
) : AbstractFormValidator<String>(errorMessagesId) {

    class Factory : FormValidatorProtocol.Factory {
        override val type: String = FormValidatorSchema.Value.Type.stringMinValue

        override fun create(
            errorMessagesId: String?,
            prototypeObject: JsonObject
        ) = StringMinValueFormValidator(
            errorMessagesId = errorMessagesId,
            minValue = prototypeObject[FormValidatorSchema.Key.Param.value].string.toInt(),
        )
    }

    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.toIntOrNull()?.let { it > minValue } ?: false
    }
}
