package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonObject

class UpdateViewUseCase(
    private val useCaseExecutor: UseCaseExecutor,
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(url: String, jsonObject: JsonObject) {
        useCaseExecutor.invoke(
            useCase = getViewState,
            input = GetViewStateUseCase.Input(
                url = url
            ),
            onResult = {
                state.update(jsonObject)
            }
        )
    }
}
