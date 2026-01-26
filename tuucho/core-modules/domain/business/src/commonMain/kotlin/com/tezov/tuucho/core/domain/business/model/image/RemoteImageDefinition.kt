package com.tezov.tuucho.core.domain.business.model.image

import com.tezov.tuucho.core.domain.business.protocol.ImageDefinitionProtocol

object RemoteImageDefinition : ImageDefinitionProtocol {
    override val command get() = "remote"
}
