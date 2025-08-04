package com.tezov.tuucho.core.presentation.ui.state

import com.tezov.tuucho.core.domain.business.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FieldsFormStateProtocol
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class FieldsFormState : FieldsFormStateProtocol {

    private data class Entry(
        var value: String,
        val validators: List<FieldValidatorProtocol<String>>?,
    )

    private val fields = mutableMapOf<String, Entry>()

    override fun clear() {
        fields.clear()
    }

    override fun removeField(id: String) {
        fields.remove(id)
    }

    override fun addField(
        id: String,
        initialValue: String,
        validators: List<FieldValidatorProtocol<String>>?,
    ) {
        if (fields.containsKey(id)) {
            throw UiException.Default("id $id already exist")
        }
        fields[id] = Entry(initialValue, validators)
    }

    override fun updateField(id: String, value: String) {
        fields[id]!!.value = value
    }

    override fun getFieldOrNull(id: String) = fields[id]?.value

    override fun updateAllValidity() {
        fields.forEach { entry ->
            entry.value.updateValidity()
        }
    }

    override fun isAllValid() = fields.all { entry ->
        entry.value.isValid()
    }

    override fun updateValidity(id: String) {
        fields[id]?.updateValidity()
    }

    override fun isValid(id: String) = fields[id]?.isValid()

    override fun getAllValidityResult() = mutableListOf<Pair<String, Boolean>>().apply {
        fields.forEach { entry ->
            add(Pair(entry.key, entry.value.isValid()))
        }
    }

    private fun Entry.updateValidity() {
        validators?.forEach {
            it.updateValidity(value)
        }
    }

    private fun Entry.isValid() = validators?.all { it.isValid() } ?: true

    override fun data() = mutableMapOf<String, JsonElement>().apply {
        fields.forEach { (key, value) ->
            value.value.let { this[key] = JsonPrimitive(it) }
        }
    }.let(::JsonObject)

}