package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject

class RetrieveOnDemandMaterialRepository(
    private val materialShadower: MaterialShadower
) {

    private val _events = MutableSharedFlow<JsonObject>(replay = 0)
    val events: SharedFlow<JsonObject> = _events

    suspend fun process(materialElement: JsonObject) {

        materialShadower.process(materialElement)

        //TODO ICI
        // to the MaterialShadower that return something like Part
        // then process them here.

    }
}
