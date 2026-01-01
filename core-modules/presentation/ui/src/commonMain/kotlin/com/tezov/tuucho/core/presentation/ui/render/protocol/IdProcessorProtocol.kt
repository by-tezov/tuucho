package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface IdProcessorProtocol : HasIdProtocol {
    suspend fun process(
        jsonElement: JsonElement?
    )
}
