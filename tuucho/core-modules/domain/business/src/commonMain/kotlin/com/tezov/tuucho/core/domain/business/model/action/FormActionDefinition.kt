package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object FormActionDefinition {
    private const val command = "form"

    object Send : ActionDefinitionProtocol {
        override val lockable get() = NavigateActionDefinition.Url.lockable + NavigateActionDefinition.LocalDestination.lockable

        override val command get() = FormActionDefinition.command

        override val authority = "send-url"
    }

    object Update : ActionDefinitionProtocol {
        override val lockable get() = InteractionLockable.Empty

        override val command get() = FormActionDefinition.command

        override val authority = "update"

        object Target {
            const val error = "error"
        }
    }
}
