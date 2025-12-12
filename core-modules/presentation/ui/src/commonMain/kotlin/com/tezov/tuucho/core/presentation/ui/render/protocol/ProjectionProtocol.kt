package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface ProjectionProtocol {
    val isReady: Boolean?

    val key: String

    suspend fun process(
        jsonElement: JsonElement?
    )
}

