package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class AddViewUseCase(
    private val useCaseExecutor: UseCaseExecutor,
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(url: String, screen: ViewProtocol) {
        useCaseExecutor.invoke(
            useCase = getViewState,
            input = GetViewStateUseCase.Input(
                url = url
            ),
            onResult = {
                state.views.add(screen)
            }
        )
    }

}