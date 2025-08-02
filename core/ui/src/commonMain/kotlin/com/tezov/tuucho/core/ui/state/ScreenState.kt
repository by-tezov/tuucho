package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormsStateProtocol
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