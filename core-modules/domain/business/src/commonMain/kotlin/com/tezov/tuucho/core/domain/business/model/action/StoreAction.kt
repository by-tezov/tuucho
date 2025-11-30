package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object StoreAction {
    private const val command = "store"

    object KeyValue : ActionProtocol {
        override val lockable get() = InteractionLockable.Empty

        override val command get() = StoreAction.command

        override val authority = "key-value"

        object Target {
            const val save = "save"
            const val remove = "remove"
        }
    }
}
