package com.tezov.tuucho.core.domain.business.usecase.state

import com.tezov.tuucho.core.domain.business.protocol.state.ScreenStateProtocol
import kotlinx.serialization.json.JsonObject

class UpdateViewUseCase(
    private val screenState: ScreenStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(url: String, jsonObject: JsonObject) {
        screenState.update(jsonObject)
    }
}
