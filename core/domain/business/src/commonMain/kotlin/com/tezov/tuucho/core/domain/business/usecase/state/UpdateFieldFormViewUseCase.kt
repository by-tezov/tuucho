package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class UpdateFieldFormViewUseCase(
    private val useCaseExecutor: UseCaseExecutor,
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
        value: String,
    ) {
        useCaseExecutor.invoke(
            useCase = getViewState,
            input = GetViewStateUseCase.Input(
                url = url
            ),
            onResult = {
                state.form()
                    .fields()
                    .updateField(
                        id = id,
                        value = value
                    )
            }
        )
    }

}