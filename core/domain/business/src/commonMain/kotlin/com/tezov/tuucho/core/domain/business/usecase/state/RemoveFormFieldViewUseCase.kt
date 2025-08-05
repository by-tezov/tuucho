package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class RemoveFormFieldViewUseCase(
    private val useCaseExecutor: UseCaseExecutor,
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
    ) {
        useCaseExecutor.invoke(
            useCase = getViewState,
            input = GetViewStateUseCase.Input(
                url = url
            ),
            onResult = {
                state.form()
                    .fields()
                    .removeField(
                        id = id
                    )
            }
        )
    }

}