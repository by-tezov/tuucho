package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol

object FormAction {
    const val command = "form"
    private val locks = (NavigateAction.Url.locks + NavigateAction.LocalDestination.locks).distinct()

    object Send : ActionProtocol {
        override val locks get() = FormAction.locks

        override val command get() = FormAction.command

        override val authority = "send-url"

        object ActionLabel {
            const val validated = "validated"
            const val denied = "denied"
        }
    }

    object Update : ActionProtocol {
        override val locks get() = FormAction.locks

        override val command get() = FormAction.command

        override val authority = "update"

        object Target {
            const val error = "error"
        }
    }
}
