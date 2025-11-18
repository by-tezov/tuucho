package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type

object NavigateAction {
    const val command = "navigate"
    private val locks = listOf(Type.ScreenInteraction, Type.Navigation)

    object Url : ActionProtocol {
        override val locks get() = NavigateAction.locks

        override val command get() = NavigateAction.command

        override val authority = "url"
    }

    object LocalDestination : ActionProtocol {
        override val locks get() = NavigateAction.locks

        override val command get() = NavigateAction.command

        override val authority = "local-destination"

        object Target {
            const val back = "back"
            const val finish = "finish"
        }
    }
}
