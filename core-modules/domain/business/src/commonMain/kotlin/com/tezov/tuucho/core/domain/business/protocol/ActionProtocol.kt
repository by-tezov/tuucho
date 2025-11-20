package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock

interface ActionProtocol {
    val command: String
    val authority: String
    val locks: List<InteractionLock.Type>
}
