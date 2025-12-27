package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface UpdatableProtocol {
    val type: String

    val id: String?

    val isReady: Boolean

    var onStatusChanged: () -> Unit

    suspend fun process(
        jsonElement: JsonElement?
    )
}
