package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import kotlinx.serialization.json.JsonElement

interface ProcessorProjectorProtocol {
    val type: String

    suspend fun process(
        jsonElement: JsonElement?
    )
}
