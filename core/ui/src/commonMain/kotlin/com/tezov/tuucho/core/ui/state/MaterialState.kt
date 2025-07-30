package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormMaterialStateProtocol
import kotlinx.serialization.json.JsonObject

class MaterialState(
    private val formMaterialState: FormMaterialStateProtocol
) : MaterialStateProtocol {

    override val screens: MutableList<ScreenProtocol> = mutableListOf()

    override fun update(jsonObject: JsonObject) {
        screens.forEach { it: ScreenProtocol ->
            it.update(jsonObject)
        }
    }

    override fun formState() = formMaterialState


}