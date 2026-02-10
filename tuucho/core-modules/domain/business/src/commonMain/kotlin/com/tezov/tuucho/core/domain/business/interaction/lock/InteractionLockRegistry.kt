package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

class InteractionLockRegistry(
    actionDefinitions: List<ActionDefinitionProtocol>
) : InteractionLockProtocol.Registry {
    private val storage = buildMap {
        actionDefinitions.forEach { register(it) }
    }

    private fun MutableMap<Pair<String, String?>, InteractionLockable.Types>.register(
        action: ActionDefinitionProtocol,
    ) {
        when (val lockable = action.lockable) {
            is InteractionLockable.Types -> {
                val key = action.command to action.authority
                this[key] = lockable
            }

            InteractionLockable.Empty -> { // nothing
            }

            else -> {
                throw DomainException.Default("Provide InteractionLockable.Type or InteractionLockable.Empty only")
            }
        }
    }

    override fun lockTypeFor(
        command: String,
        authority: String?
    ) = storage[command to authority] ?: InteractionLockable.Empty
}
