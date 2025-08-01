package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.state.form.FieldsFormStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormsStateProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class FormsState(
    private val fieldsFormState: FieldsFormStateProtocol,
) : FormsStateProtocol {

    override fun fields() = fieldsFormState

    override fun updateAllValidity() {
        fieldsFormState.updateAllValidity()
    }

    override fun isAllValid(): Boolean {
        return fieldsFormState.isAllValid()
    }

    override fun updateValidity(id: String) {
        fieldsFormState.updateValidity(id)
    }

    override fun isValid(id: String): Boolean? {
        return fieldsFormState.isValid(id)
    }

    override fun getAllValidityResult(): List<Pair<String, Boolean>> {
        return fieldsFormState.getAllValidityResult()
    }

    override fun data() = mutableMapOf<String, JsonElement>().apply {
        put("fields", fieldsFormState.data())
    }.let(::JsonObject)

}