package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object NavigateAction {
    const val command = "navigate"
    private val lockable
        get() = InteractionLockable.Type(
            listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
        )

    object Url : ActionProtocol {
        override val lockable get() = NavigateAction.lockable

        override val command get() = NavigateAction.command

        override val authority = "url"
    }

    object LocalDestination : ActionProtocol {
        override val lockable get() = NavigateAction.lockable

        override val command get() = NavigateAction.command

        override val authority = "local-destination"

        object Target {
            const val back = "back"
            const val finish = "finish"
        }
    }
}
