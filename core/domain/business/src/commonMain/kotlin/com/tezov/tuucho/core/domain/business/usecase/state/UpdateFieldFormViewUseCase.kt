package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase

class UpdateFieldFormViewUseCase(
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(
        url: String,
        id: String,
        value: String,
    ) {
        val stateView = getViewState.invoke(url)
        stateView
            .form()
            .fields()
            .updateField(
                id = id,
                value = value
            )
    }

}