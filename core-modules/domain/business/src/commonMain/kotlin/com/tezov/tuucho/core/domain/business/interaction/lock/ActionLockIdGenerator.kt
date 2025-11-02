package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import kotlin.uuid.Uuid

internal class ActionLockIdGenerator : IdGeneratorProtocol {
    override fun generate() = Uuid.Companion.random().toHexString()
}
