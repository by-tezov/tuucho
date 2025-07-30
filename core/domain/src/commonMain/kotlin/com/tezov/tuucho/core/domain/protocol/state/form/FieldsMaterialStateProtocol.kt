package com.tezov.tuucho.core.domain.protocol.state.form

import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol

interface FieldsMaterialStateProtocol: FormStateProtocol {

    fun removeField(
        id: String,
    )

    fun addField(
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null
    )

    fun updateField(id: String, value: String)

    fun getFieldOrNull(id: String): String?

}