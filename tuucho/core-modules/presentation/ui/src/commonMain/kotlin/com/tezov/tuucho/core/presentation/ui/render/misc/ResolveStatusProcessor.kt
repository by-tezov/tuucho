package com.tezov.tuucho.core.presentation.ui.render.misc

import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateInvokerProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement

class ResolveStatusProcessor : ResolveStatusProcessorProtocol {
    override var hasBeenResolved: Boolean? = null
        private set

    override var readyStatusInvalidateInvoker: ReadyStatusInvalidateInvokerProtocol? = null
        private set

    override fun update(
        jsonElement: JsonElement?
    ) {
        val previous = hasBeenResolved
        if (jsonElement != null) {
            hasBeenResolved = jsonElement.idSourceOrNull == null
        }
        if (previous != hasBeenResolved) {
            readyStatusInvalidateInvoker?.invalidateReadyStatus()
        }
    }

    override fun setReadyStatusInvalidateInvoker(
        value: ReadyStatusInvalidateInvokerProtocol
    ) {
        readyStatusInvalidateInvoker = value
    }
}
