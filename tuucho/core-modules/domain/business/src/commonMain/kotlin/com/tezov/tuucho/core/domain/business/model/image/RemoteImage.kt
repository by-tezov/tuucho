package com.tezov.tuucho.core.domain.business.model.image

import com.tezov.tuucho.core.domain.business.protocol.ImageProtocol

object RemoteImage: ImageProtocol {

    override val command get() = "remote"

    object Target {
        const val save = "save"
        const val remove = "remove"
    }
}
