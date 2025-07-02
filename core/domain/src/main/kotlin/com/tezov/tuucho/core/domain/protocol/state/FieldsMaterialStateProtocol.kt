package com.tezov.tuucho.core.domain.protocol.state

import kotlinx.serialization.json.JsonElement

interface FieldsMaterialStateProtocol {

    fun clear()

    fun addOrUpdateField(id: String, value: String)

    fun getFieldOrNull(id: String): String?

    fun getFields(): Map<String, String>

    fun isAllValid(): Boolean

    fun isValid(id: String): Boolean?

    fun data(): JsonElement
}