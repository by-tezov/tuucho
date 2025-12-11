package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface UpdatableProcessorProtocol : HasIdProtocol {
    val type: String

    suspend fun process(
        jsonElement: JsonElement?
    )
}
