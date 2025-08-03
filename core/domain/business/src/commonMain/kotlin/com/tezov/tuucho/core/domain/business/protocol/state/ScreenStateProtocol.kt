package com.tezov.tuucho.core.domain.business.protocol.state

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateProtocol
import kotlinx.serialization.json.JsonObject

interface ScreenStateProtocol {

    var url: String

    val views: MutableList<ViewProtocol>

    fun clear() {
        url = ""
        views.clear()
        form().clear()
    }

    fun update(jsonObject: JsonObject)

    fun form(): FormsStateProtocol

}