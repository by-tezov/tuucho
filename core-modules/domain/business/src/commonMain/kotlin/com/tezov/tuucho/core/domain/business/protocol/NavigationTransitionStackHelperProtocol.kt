package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.serialization.json.JsonObject

interface NavigationTransitionStackHelperProtocol {

    fun isForeground(
        transitionSpec: JsonObject,
    ): Boolean

}

