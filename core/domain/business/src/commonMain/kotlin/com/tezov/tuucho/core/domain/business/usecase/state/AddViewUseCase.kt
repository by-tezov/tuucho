package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase

class AddViewUseCase(
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(url: String, screen: ViewProtocol) {
        val stateView = getViewState.invoke(url)
        stateView.views.add(screen)
    }

}