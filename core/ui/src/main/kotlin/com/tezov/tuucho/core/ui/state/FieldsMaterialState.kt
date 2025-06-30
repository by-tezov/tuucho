package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.state.FieldsMaterialStateProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class FieldsMaterialState : FieldsMaterialStateProtocol {

    private val fields = mutableMapOf<String, String>()

    @Synchronized
    override fun clear() {
        fields.clear()
    }

    override fun addOrUpdateField(id: String, value: String) {
        fields[id] = value
    }

    override fun getFieldOrNull(id: String) = fields[id]

    override fun getFields() = fields.toMap()

    override fun isAllValid(): Boolean {
        return true //TODO
    }

    override fun isValid(id: String): Boolean? {
        TODO("Not yet implemented")
    }

    override fun data() = mutableMapOf<String, JsonElement>() .apply {
        fields.forEach { (key, value) ->
            this[key] = JsonPrimitive(value)
        }
    }.let(::JsonObject)

}