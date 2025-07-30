package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class ClearFormInMaterialStateUseCase(
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String
    ) {
        materialState
            .formState()
            .fieldsState()
            .removeField(
                id = id
            )
    }

}