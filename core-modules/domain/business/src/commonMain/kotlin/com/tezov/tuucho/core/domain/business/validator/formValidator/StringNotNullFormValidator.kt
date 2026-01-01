package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringNotNullFormValidator(
    errorMessagesId: String?,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = !value.isNullOrBlank()
    }
}
