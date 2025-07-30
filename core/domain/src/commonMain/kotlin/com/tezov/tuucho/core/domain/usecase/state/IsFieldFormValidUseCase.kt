package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class IsFieldFormValidUseCase(
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String
    ): Boolean? {
        return materialState
            .formState()
            .fieldsState()
            .isValid(id)
    }

}