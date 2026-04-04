package com.tezov.tuucho.core.presentation.ui.protocol

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
