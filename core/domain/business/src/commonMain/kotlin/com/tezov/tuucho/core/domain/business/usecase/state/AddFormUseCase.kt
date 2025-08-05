package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class AddFormUseCase(
    private val useCaseExecutor: UseCaseExecutor,
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
        initialValue: String = "",
        validators: List<FieldValidatorProtocol<String>>? = null,
    ) {
        useCaseExecutor.invoke(
            useCase = getViewState,
            input = GetViewStateUseCase.Input(
                url = url
            ),
            onResult = {
                state.form()
                    .fields()
                    .addField(
                        id = id,
                        initialValue = initialValue,
                        validators = validators
                    )
            }
        )
    }

}