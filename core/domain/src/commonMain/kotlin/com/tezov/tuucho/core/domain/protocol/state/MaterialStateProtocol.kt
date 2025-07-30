package com.tezov.tuucho.core.domain.protocol.state

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormMaterialStateProtocol
import kotlinx.serialization.json.JsonObject

interface MaterialStateProtocol {

    val screens: MutableList<ScreenProtocol>

    fun clear() {
        screens.clear()
        formState().clear()
    }

    fun update(jsonObject: JsonObject)

    fun formState(): FormMaterialStateProtocol

}