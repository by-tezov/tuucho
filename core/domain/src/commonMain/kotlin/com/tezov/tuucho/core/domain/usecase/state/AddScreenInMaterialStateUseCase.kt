package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class AddScreenInMaterialStateUseCase(
    private val materialStateProtocol: MaterialStateProtocol,
) {

    fun invoke(screen: ScreenProtocol) {
        materialStateProtocol.screens.add(screen)
    }

}