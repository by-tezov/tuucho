package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface ReadyStatusProtocol {
    val isReady: Boolean

    var onStatusChanged: () -> Unit

    fun updateIsReady(
        jsonElement: JsonElement?
    )
}
