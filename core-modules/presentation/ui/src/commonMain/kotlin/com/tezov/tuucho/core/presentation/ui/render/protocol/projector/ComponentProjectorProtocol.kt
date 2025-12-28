package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import kotlinx.serialization.json.JsonElement

interface ComponentProjectorProtocol : HasUpdatableProtocol {

    fun add(
        projector: ProjectorProtocol
    )

    suspend fun process(
        jsonElement: JsonElement?
    )
}
