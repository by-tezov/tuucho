package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class AddScreenInMaterialStateUseCase(
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(url: String, screen: ScreenProtocol) {
        materialState.screens.add(screen)
    }

}