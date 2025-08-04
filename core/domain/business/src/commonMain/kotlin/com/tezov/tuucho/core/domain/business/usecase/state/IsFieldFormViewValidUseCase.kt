package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase

class IsFieldFormViewValidUseCase(
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
    ): Boolean? {
        val stateView = getViewState.invoke(url)
        return stateView
            .form()
            .fields()
            .isValid(id)
    }

}