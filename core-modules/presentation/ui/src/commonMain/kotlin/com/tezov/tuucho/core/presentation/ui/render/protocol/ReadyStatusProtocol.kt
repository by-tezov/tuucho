package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface ReadyStatusProtocol : HasReadyStatusProtocol {

    fun update(
        jsonElement: JsonElement?
    ) {
    }
}
