package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import kotlinx.serialization.json.JsonElement

interface ComponentProcessorProjectorProtocol : HasUpdatableProtocol {
    fun add(
        projector: ProcessorProjectorProtocol
    )

    suspend fun process(
        jsonElement: JsonElement?
    )
}
