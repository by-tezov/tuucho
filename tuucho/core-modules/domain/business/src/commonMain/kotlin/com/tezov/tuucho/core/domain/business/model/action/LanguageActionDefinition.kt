package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object LanguageActionDefinition {
    private const val command = "language"
    private val lockable
        get() = InteractionLockable.Types(
            listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
        )

    object Current : ActionDefinitionProtocol {
        override val lockable get() = LanguageActionDefinition.lockable

        override val command get() = LanguageActionDefinition.command

        override val authority = "current"

        object Query {
            const val code = "code"
            const val country = "country"
        }
    }

    object System : ActionDefinitionProtocol {
        override val lockable get() = LanguageActionDefinition.lockable

        override val command get() = LanguageActionDefinition.command

        override val authority = "system"
    }
}
