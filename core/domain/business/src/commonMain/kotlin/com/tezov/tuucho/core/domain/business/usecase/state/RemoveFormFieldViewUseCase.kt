package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase

class RemoveFormFieldViewUseCase(
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
    ) {
        val stateView = getViewState.invoke(url)
        stateView
            .form()
            .fields()
            .removeField(
                id = id
            )
    }

}