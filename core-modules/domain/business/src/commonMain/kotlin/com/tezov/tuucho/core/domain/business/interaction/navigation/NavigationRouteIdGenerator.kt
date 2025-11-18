package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol

class NavigationRouteIdGenerator internal constructor(
    private val idGenerator: IdGeneratorProtocol<Unit, String>
) : IdGeneratorProtocol<Unit, String> {
    override fun generate() = idGenerator.generate()
}
