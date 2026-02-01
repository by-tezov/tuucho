package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType

interface InteractionLockMonitor {
    enum class Event {
        Acquired,
        AcquireFromTry,
        WaitToAcquire,
        Released,
        CanNotBeReleased,
        TryAcquireAgain
    }

    data class Context(
        val event: Event,
        val requester: List<String>,
        val lockTypes: List<InteractionLockType>,
    )

    suspend fun process(
        context: Context
    )
}
