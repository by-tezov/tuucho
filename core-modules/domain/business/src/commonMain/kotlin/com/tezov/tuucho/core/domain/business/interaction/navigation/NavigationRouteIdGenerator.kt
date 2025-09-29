package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import kotlin.uuid.Uuid

class NavigationRouteIdGenerator: IdGeneratorProtocol {

    override fun generate() = Uuid.Companion.random().toHexString()

}