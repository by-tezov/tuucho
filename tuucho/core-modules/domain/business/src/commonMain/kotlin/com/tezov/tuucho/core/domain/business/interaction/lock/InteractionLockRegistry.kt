package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

class InteractionLockRegistry : InteractionLockProtocol.Registry {
    private val storage = mutableMapOf<Pair<String, String?>, InteractionLockable.Type>()

    override fun register(
        action: ActionProtocol,
    ) {
        when (val lockable = action.lockable) {
            is InteractionLockable.Type -> {
                val key = action.command to action.authority
                if (storage.containsKey(key)) {
                    throw DomainException.Default("Lock type already registered for $key")
                }
                storage[key] = lockable
            }

            InteractionLockable.Empty -> { /* nothing */ }

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
