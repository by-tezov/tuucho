package com.tezov.tuucho.sample.uiExtension.domain

import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

object EchoMessageCustomActionDefinition : ActionDefinitionProtocol {
    override val lockable get() = InteractionLockable.Empty

    override val command get() = "echo-message-custom-action"

    override val authority = null
}