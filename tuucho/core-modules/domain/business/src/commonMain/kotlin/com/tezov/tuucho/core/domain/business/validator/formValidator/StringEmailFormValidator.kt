package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import kotlinx.serialization.json.JsonObject

class StringEmailFormValidator(
    errorMessagesId: String?,
) : AbstractFormValidator<String>(errorMessagesId) {

    class Factory : FormValidatorProtocol.Factory {
        override val type: String = FormValidatorSchema.Value.Type.stringEmail

        override fun create(
            errorMessagesId: String?,
            prototypeObject: JsonObject
        ) = StringEmailFormValidator(
            errorMessagesId = errorMessagesId,
        )
    }

    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
    }
}
