package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object NavigateActionDefinition {
    private const val command = "navigate"
    private val lockable
        get() = InteractionLockable.Type(
            listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
        )

    object Url : ActionDefinitionProtocol {
        override val lockable get() = NavigateActionDefinition.lockable

        override val command get() = NavigateActionDefinition.command

        override val authority = "url"
    }

    object LocalDestination : ActionDefinitionProtocol {
        override val lockable get() = NavigateActionDefinition.lockable

        override val command get() = NavigateActionDefinition.command

        override val authority = "local-destination"

        object Target {
            const val back = "back"
            const val finish = "finish"
            const val current = "current"
        }
    }
}
