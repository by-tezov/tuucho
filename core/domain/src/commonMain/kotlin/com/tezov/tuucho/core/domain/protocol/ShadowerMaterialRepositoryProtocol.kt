package com.tezov.tuucho.core.domain.protocol

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject

interface ShadowerMaterialRepositoryProtocol {

    data class Event(
        val type: String,
        val url: String,
        val jsonObject: JsonObject
    )

    val events: SharedFlow<Event>
}