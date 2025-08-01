package com.tezov.tuucho.core.domain.protocol.state

import com.tezov.tuucho.core.domain.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormsStateProtocol
import kotlinx.serialization.json.JsonObject

interface ScreenStateProtocol {

    val views: MutableList<ViewProtocol>

    fun clear() {
        views.clear()
        form().clear()
    }

    fun update(jsonObject: JsonObject)

    fun form(): FormsStateProtocol

}