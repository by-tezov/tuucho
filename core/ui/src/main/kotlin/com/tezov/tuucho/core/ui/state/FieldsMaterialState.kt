package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.ValidatorProtocol
import com.tezov.tuucho.core.domain.protocol.state.FieldsMaterialStateProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class FieldsMaterialState : FieldsMaterialStateProtocol {

    private data class Entry(
        var value: String,
        val validators: List<ValidatorProtocol<String>>?
    )

    private val fields = mutableMapOf<String, Entry>()

    @Synchronized
    override fun clear() {
        fields.clear()
    }

    override fun addField(
        id: String,
        initialValue: String,
        validators: List<ValidatorProtocol<String>>?
    ) {
        if (fields.containsKey(id)) {
            throw IllegalStateException("id $id already exist")
        }
        fields[id] = Entry(initialValue, validators)
    }

    override fun updateField(id: String, value: String) {
        fields[id]!!.value = value
    }

    override fun getFieldOrNull(id: String) = fields[id]?.value

     override fun isAllValid() = fields.all { entry ->
        entry.value.isValid()
    }

    override fun isValid(id: String) = fields[id]?.isValid()

    private fun Entry.isValid(): Boolean {
        return validators?.all {
            it.apply { updateValidity(value) }.isValid()
        } ?: true
    }

    override fun data() = mutableMapOf<String, JsonElement>().apply {
        fields.forEach { (key, value) ->
            value.value.let { this[key] = JsonPrimitive(it) }
        }
    }.let(::JsonObject)

}