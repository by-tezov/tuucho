package com.tezov.tuucho.sample.uiExtension.domain

import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object EchoMessageCustomAction : ActionProtocol {
    override val lockable get() = InteractionLockable.Empty

    override val command get() = "echo-message-custom-action"

    override val authority = null
}