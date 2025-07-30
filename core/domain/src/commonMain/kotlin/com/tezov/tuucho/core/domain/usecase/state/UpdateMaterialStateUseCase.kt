package com.tezov.tuucho.core.domain.usecase.state

import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import kotlinx.serialization.json.JsonObject

class UpdateMaterialStateUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val materialState: MaterialStateProtocol,
) {

    //TODO: inject stack navigation repo, find materialState with url instead of injecting a singleton

    fun invoke(url: String, jsonObject: JsonObject) {
        materialState.update(jsonObject)
    }
}
