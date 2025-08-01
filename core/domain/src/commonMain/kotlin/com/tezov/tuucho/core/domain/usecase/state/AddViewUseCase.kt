package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol

class AddViewUseCase(
    private val screenState: ScreenStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(url: String, screen: ViewProtocol) {
        screenState.views.add(screen)
    }

}