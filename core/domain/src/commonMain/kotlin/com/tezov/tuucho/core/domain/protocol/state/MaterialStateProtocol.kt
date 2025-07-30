package com.tezov.tuucho.core.domain.protocol.state

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import kotlinx.serialization.json.JsonObject

interface MaterialStateProtocol {

    var url: String

    val screens: MutableList<ScreenProtocol>

    fun clear() {
        url = ""
        screens.clear()
        form().clear()
    }

    suspend fun update(url: String, jsonObject: JsonObject)

    fun form(): FormMaterialStateProtocol


}