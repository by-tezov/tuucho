package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.HasContextualUpdaterProtocol
import kotlinx.serialization.json.JsonElement

interface ComponentProcessorProjectorProtocol : HasContextualUpdaterProtocol {
    fun add(
        projector: ProcessorProjectorProtocol
    )

    suspend fun process(
        jsonElement: JsonElement?
    )
}
