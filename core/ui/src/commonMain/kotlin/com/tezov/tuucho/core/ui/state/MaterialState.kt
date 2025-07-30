package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import kotlinx.serialization.json.JsonObject

class MaterialState(
    private val formMaterialState: FormMaterialStateProtocol
) : MaterialStateProtocol {

    override var url: String = ""
    override val screens: MutableList<ScreenProtocol> = mutableListOf()

    override suspend fun update(url: String, jsonObject: JsonObject) {
        //TODO check url ok
        screens.forEach { it: ScreenProtocol ->
            it.update(jsonObject)
        }
    }

    override fun form() = formMaterialState


}