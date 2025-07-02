package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.state.FieldsMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class FormMaterialState(
    private val fieldsMaterialState: FieldsMaterialStateProtocol
) : FormMaterialStateProtocol {

    override fun fieldsState() = fieldsMaterialState

    override fun isAllValid(): Boolean {
        return fieldsMaterialState.isAllValid()
    }

    override fun isValid(id: String): Boolean? {
        TODO("Not yet implemented")
    }

    override fun data() = mutableMapOf<String, JsonElement>().apply {
        put("fields", fieldsMaterialState.data())
    }.let(::JsonObject)

}