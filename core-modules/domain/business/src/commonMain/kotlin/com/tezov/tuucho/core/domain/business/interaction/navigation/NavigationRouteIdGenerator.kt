package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator.Id
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol

class NavigationRouteIdGenerator internal constructor(
    private val idGenerator: IdGeneratorProtocol<Unit, String>
) : IdGeneratorProtocol<Unit, Id> {
    @JvmInline
    value class Id(
        val value: String
    )

    override fun generate() = Id(
        value = idGenerator.generate()
    )
}
