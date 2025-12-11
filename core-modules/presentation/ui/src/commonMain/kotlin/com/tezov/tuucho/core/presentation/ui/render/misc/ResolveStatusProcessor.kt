package com.tezov.tuucho.core.presentation.ui.render.misc

import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.RequestViewUpdateInvokerProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement

class ResolveStatusProcessor : ResolveStatusProcessorProtocol {
    override var hasBeenResolved: Boolean? = null
        private set

    override var requestViewUpdateInvoker: RequestViewUpdateInvokerProtocol? = null
        private set

    override fun update(
        jsonElement: JsonElement?
    ) {
        val previous = hasBeenResolved
        if(jsonElement != null) {
            hasBeenResolved = jsonElement.idSourceOrNull == null
        }
        if (previous != hasBeenResolved) {
            requestViewUpdateInvoker?.invokeRequestViewUpdate()
        }
    }

    override fun setRequestViewUpdater(value: RequestViewUpdateInvokerProtocol) {
        requestViewUpdateInvoker = value
    }
}
