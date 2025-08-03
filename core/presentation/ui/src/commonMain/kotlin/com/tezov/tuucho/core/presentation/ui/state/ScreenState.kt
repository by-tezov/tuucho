package com.tezov.tuucho.core.presentation.ui.state

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.ScreenStateProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateProtocol
import kotlinx.serialization.json.JsonObject

class ScreenState(
    private val formsState: FormsStateProtocol
) : ScreenStateProtocol {

    override var url: String = ""

    override val views: MutableList<ViewProtocol> = mutableListOf()

    override fun update(jsonObject: JsonObject) {
        views.forEach { it: ViewProtocol ->
            it.update(jsonObject)
        }
    }

    override fun form() = formsState


}