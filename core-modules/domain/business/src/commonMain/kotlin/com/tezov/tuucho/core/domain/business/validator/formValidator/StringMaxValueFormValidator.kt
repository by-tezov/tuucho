package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringMaxValueFormValidator(
    errorMessagesId: String?,
    private val maxValue: Int,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.toIntOrNull()?.let { it < maxValue } ?: false
    }
}
