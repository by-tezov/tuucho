package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.tool.async.Notifier.Collector
import kotlinx.serialization.json.JsonObject

interface ShadowerMaterialRepositoryProtocol {

    data class Event(
        val type: String,
        val url: String,
        val jsonObject: JsonObject,
    )

    val events: Collector<Event>
}