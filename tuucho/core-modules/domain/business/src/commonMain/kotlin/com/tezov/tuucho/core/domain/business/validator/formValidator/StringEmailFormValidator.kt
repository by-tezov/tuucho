package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringEmailFormValidator(
    errorMessagesId: String?,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
    }
}
