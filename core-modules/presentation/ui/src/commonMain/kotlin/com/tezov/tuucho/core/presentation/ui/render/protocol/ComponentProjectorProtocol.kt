package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface ComponentProjectorProtocol {

    fun add(projector: ProjectorProtocol)

    suspend fun process(
        jsonElement: JsonElement?
    )

}
