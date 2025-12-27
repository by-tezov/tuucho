package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface ProjectableProtocol {
    val keys: Set<String>

    var onStatusChanged: (Boolean) -> Unit

    suspend fun process(
        jsonElement: JsonElement?,
        key: String
    )
}
