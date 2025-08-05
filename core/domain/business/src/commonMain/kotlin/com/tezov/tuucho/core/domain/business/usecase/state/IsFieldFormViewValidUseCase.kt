package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class IsFieldFormViewValidUseCase(
    private val useCaseExecutor: UseCaseExecutor,
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
    ): Boolean? {
//        useCaseExecutor.invoke(
//            useCase = getViewState,
//            input = GetViewStateUseCase.Input(
//                url = url
//            ),
//            onResult = { output ->
//                output.state.form()
//                    .fields()
//                    .isValid(id)
//            }
//        )
        return true
    }

}