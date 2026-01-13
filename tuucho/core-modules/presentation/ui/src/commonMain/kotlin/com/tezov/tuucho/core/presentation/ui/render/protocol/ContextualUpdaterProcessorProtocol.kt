package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface HasContextualUpdaterProtocol {
    val contextualUpdater: List<ContextualUpdaterProcessorProtocol>
}

interface ContextualUpdaterProcessorProtocol : HasIdProtocol {
    val type: String

    suspend fun process(
        jsonElement: JsonElement?
    )
}
