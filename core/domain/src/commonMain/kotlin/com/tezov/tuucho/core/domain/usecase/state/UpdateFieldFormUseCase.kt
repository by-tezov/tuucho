package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class UpdateFieldFormUseCase(
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String,
        value: String
    ) {
        materialState
            .formState()
            .fieldsState()
            .updateField(
                id = id,
                value = value
            )
    }

}