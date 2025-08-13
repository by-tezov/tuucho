package com.tezov.tuucho.core.domain.business.navigation

import kotlin.uuid.Uuid

class NavigationRouteIdGenerator {

    fun generate() = Uuid.Companion.random().toHexString()

}