package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

interface ActionDefinitionProtocol {
    val command: String
    val authority: String?
    val lockable: InteractionLockable
}
