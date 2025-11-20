package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type

interface InteractionLockMonitor {

    enum class Event{
        Acquire,
        TryAcquire,
        WaitToAcquire,
        Release,
        CanNotBeRelease,
        TryAcquireAgain
    }

    data class Context(
        val event: Event,
        val requester: String,
        val lockTypes: List<Type>,
    )

    fun process(
        context: Context
    )
}
