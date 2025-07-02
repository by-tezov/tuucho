package com.tezov.tuucho.core.ui.state

import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class MaterialState(
    private val formMaterialState: FormMaterialStateProtocol
) : MaterialStateProtocol {

    override fun form() = formMaterialState


}