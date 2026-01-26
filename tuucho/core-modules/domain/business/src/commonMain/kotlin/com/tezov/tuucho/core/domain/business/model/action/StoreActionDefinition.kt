package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object StoreActionDefinition {
    private const val command = "store"

    object KeyValue : ActionDefinitionProtocol {
        override val lockable get() = InteractionLockable.Empty

        override val command get() = StoreActionDefinition.command

        override val authority = "key-value"

        object Target {
            const val save = "save"
            const val remove = "remove"
        }
    }
}
