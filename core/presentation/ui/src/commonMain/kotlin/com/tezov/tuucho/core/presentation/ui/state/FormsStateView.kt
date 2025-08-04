package com.tezov.tuucho.core.presentation.ui.state

import com.tezov.tuucho.core.domain.business.protocol.state.form.FieldsFormStateViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateViewProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class FormsStateView(
    private val fieldsFormStateView: FieldsFormStateViewProtocol,
) : FormsStateViewProtocol {

    override fun fields() = fieldsFormStateView

    override fun updateAllValidity() {
        fieldsFormStateView.updateAllValidity()
    }

    override fun isAllValid(): Boolean {
        return fieldsFormStateView.isAllValid()
    }

    override fun updateValidity(id: String) {
        fieldsFormStateView.updateValidity(id)
    }

    override fun isValid(id: String): Boolean? {
        return fieldsFormStateView.isValid(id)
    }

    override fun getAllValidityResult(): List<Pair<String, Boolean>> {
        return fieldsFormStateView.getAllValidityResult()
    }

    override fun data() = mutableMapOf<String, JsonElement>().apply {
        put("fields", fieldsFormStateView.data())
    }.let(::JsonObject)

}