package com.tezov.tuucho.core.domain.business.protocol.state

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateViewProtocol
import kotlinx.serialization.json.JsonObject

interface StateViewProtocol {

    val views: MutableList<ViewProtocol>

    fun update(jsonObject: JsonObject)

    fun form(): FormsStateViewProtocol

}