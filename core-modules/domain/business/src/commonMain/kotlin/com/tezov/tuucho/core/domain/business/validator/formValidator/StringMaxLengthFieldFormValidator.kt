package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringMaxLengthFieldFormValidator(
    errorMessagesId: String?,
    private val length: Int,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = value != null && value.length <= length || value == null && length != 0
    }
}
