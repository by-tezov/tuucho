package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import kotlinx.serialization.json.JsonObject

class UpdateViewUseCase(
    private val getViewState: GetViewStateUseCase,
) {

    fun invoke(url: String, jsonObject: JsonObject) {
        val stateView = getViewState.invoke(url)
        stateView.update(jsonObject)
    }
}
