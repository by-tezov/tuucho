package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol

class IsFieldFormViewValidUseCase(
    private val screenState: ScreenStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String
    ): Boolean? {
        return screenState
            .form()
            .fields()
            .isValid(id)
    }

}