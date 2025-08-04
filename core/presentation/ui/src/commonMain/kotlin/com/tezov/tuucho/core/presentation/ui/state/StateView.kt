package com.tezov.tuucho.core.presentation.ui.state

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateViewProtocol
import kotlinx.serialization.json.JsonObject

class StateView(
    private val formsStateView: FormsStateViewProtocol,
) : StateViewProtocol {

    override val views: MutableList<ViewProtocol> = mutableListOf()

    override fun update(jsonObject: JsonObject) {
        views.forEach { it: ViewProtocol ->
            it.update(jsonObject)
        }
    }

    override fun form() = formsStateView


}