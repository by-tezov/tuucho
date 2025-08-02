package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol

class UpdateFieldFormViewUseCase(
    private val screenState: ScreenStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String,
        value: String
    ) {
        screenState
            .form()
            .fields()
            .updateField(
                id = id,
                value = value
            )
    }

}