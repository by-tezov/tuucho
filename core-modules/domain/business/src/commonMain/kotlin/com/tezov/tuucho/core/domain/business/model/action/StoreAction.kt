package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock

object StoreAction {
    const val command = "store"

    object KeyValue : ActionProtocol {
        override val locks get() = emptyList<InteractionLock.Type>()

        override val command get() = StoreAction.command

        override val authority = "key-value"

        object Target {
            const val save = "save"
            const val remove = "remove"
        }
    }
}
