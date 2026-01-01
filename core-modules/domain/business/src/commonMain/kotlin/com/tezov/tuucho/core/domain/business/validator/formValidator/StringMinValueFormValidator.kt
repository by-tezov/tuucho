package com.tezov.tuucho.core.domain.business.validator.formValidator

class StringMinValueFormValidator(
    errorMessagesId: String?,
    private val minValue: Int,
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = value.isNullOrBlank() || value.toIntOrNull()?.let { it > minValue } ?: false
    }
}
