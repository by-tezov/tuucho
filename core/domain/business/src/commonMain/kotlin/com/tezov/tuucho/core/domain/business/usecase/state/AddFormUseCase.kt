package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase

class AddFormUseCase(
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null,
    ) {
        val stateView = getViewState.invoke(url)
        stateView
            .form()
            .fields()
            .addField(
                id = id,
                initialValue = initialValue,
                validators = validators
            )
    }

}