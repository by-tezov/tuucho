package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringMinDigitLengthFormValidator(
    errorMessagesId: String?,
    private val length: Int,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        val digitCount = value?.count { it.isDigit() } ?: 0
        isValid = digitCount >= length
    }
}
