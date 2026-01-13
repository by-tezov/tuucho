package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringOnlyDigitsFormValidator(
    errorMessagesId: String?,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.all { it.isDigit() } == true
    }
}
