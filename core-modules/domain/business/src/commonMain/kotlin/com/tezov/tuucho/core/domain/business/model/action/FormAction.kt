package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object FormAction {
    private const val command = "form"

    object Send : ActionProtocol {
        override val lockable get() = NavigateAction.Url.lockable + NavigateAction.LocalDestination.lockable

        override val command get() = FormAction.command

        override val authority = "send-url"

        object ActionLabel {
            const val validated = "validated"
            const val denied = "denied"
        }
    }

    object Update : ActionProtocol {
        override val lockable get() = InteractionLockable.Empty

        override val command get() = FormAction.command

        override val authority = "update"

        object Target {
            const val error = "error"
        }
    }
}
