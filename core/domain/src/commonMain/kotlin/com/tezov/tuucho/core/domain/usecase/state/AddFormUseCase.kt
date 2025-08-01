package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol

class AddFormUseCase(
    private val screenState: ScreenStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(
        url: String,
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null
    ) {
        screenState
            .form()
            .fields()
            .addField(
                id = id,
                initialValue = initialValue,
                validators = validators
            )
    }

}