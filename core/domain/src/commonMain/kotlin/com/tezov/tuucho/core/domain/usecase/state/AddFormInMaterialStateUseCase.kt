package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class AddFormInMaterialStateUseCase(
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null
    ) {
        materialState
            .formState()
            .fieldsState()
            .addField(
                id = id,
                initialValue = initialValue,
                validators = validators
            )
    }

}