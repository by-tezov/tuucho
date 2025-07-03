package com.tezov.tuucho.core.domain.protocol.state

import com.tezov.tuucho.core.domain.protocol.ValidatorProtocol
import kotlinx.serialization.json.JsonElement

interface FieldsMaterialStateProtocol {

    fun clear()

    fun addField(
        id: String,
        initialValue: String = "",
        validators: List<ValidatorProtocol<String>>? = null
    )

    fun updateField(id: String, value: String)

    fun getFieldOrNull(id: String): String?

    fun isAllValid(): Boolean

    fun isValid(id: String): Boolean?

    fun data(): JsonElement
}