package com.tezov.tuucho.core.domain.business.model.image

import com.tezov.tuucho.core.domain.business.protocol.ImageProtocol

object LocalImage : ImageProtocol {
    override val command get() = "local"
}
