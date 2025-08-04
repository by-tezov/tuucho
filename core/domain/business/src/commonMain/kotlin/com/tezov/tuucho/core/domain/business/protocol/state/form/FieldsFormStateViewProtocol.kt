package com.tezov.tuucho.core.domain.business.protocol.state.form

import com.tezov.tuucho.core.domain.business.protocol.FieldValidatorProtocol

interface FieldsFormStateViewProtocol : FormStateViewProtocol {

    fun removeField(
        id: String,
    )

    fun addField(
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null,
    )

    fun updateField(id: String, value: String)

    fun getFieldOrNull(id: String): String?

}