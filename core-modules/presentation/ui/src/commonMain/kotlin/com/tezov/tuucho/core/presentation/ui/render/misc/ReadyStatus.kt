package com.tezov.tuucho.core.presentation.ui.render.misc

import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import kotlinx.serialization.json.JsonElement

class ReadyStatus() : ReadyStatusProtocol {

    override var isReady = defaultStatus
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
