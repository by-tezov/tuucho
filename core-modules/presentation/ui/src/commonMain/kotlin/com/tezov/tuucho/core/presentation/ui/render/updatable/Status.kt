package com.tezov.tuucho.core.presentation.ui.render.updatable

import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.StatusProtocol
import kotlinx.serialization.json.JsonElement

class Status() : StatusProtocol {

    override var isReady = false
        private set

    override lateinit var onStatusChanged: () -> Unit

    override fun update(
        jsonElement: JsonElement?
    ) {
        val previous = isReady
        isReady = jsonElement != null && jsonElement.idSourceOrNull == null
        if (previous != isReady && this::onStatusChanged.isInitialized) {
            onStatusChanged.invoke()
        }
    }
}
