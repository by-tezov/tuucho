package com.tezov.tuucho.core.domain.protocol.state

import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import kotlinx.serialization.json.JsonElement

interface FieldsMaterialStateProtocol {

    fun clear()

    fun addField(
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null
    )

    fun updateField(id: String, value: String)

    fun getFieldOrNull(id: String): String?

    fun updateAllValidity()

    fun isAllValid(): Boolean

    fun updateValidity(id: String)

    fun isValid(id: String): Boolean?

    fun getAllValidityResult(): List<Pair<String, Boolean>>

    fun data(): JsonElement
}