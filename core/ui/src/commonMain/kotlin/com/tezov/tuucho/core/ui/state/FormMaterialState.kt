package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.state.form.FieldsMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormMaterialStateProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class FormMaterialState(
    private val fieldsMaterialState: FieldsMaterialStateProtocol,
) : FormMaterialStateProtocol {

    override fun fieldsState() = fieldsMaterialState

    override fun updateAllValidity() {
        fieldsMaterialState.updateAllValidity()
    }

    override fun isAllValid(): Boolean {
        return fieldsMaterialState.isAllValid()
    }

    override fun updateValidity(id: String) {
        fieldsMaterialState.updateValidity(id)
    }

    override fun isValid(id: String): Boolean? {
        return fieldsMaterialState.isValid(id)
    }

    override fun getAllValidityResult(): List<Pair<String, Boolean>> {
        return fieldsMaterialState.getAllValidityResult()
    }

    override fun data() = mutableMapOf<String, JsonElement>().apply {
        put("fields", fieldsMaterialState.data())
    }.let(::JsonObject)

}