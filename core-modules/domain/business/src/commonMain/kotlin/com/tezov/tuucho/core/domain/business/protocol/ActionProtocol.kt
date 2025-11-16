package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type

interface ActionProtocol {
    val command: String
    val authority: String
    val locks: List<Type>
}
