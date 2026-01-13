package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface ProjectionProcessorProtocol {
    val key: String

    suspend fun process(
        jsonElement: JsonElement?
    )
}
