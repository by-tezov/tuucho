package com.tezov.tuucho.core.domain.business.navigation.transition

import com.tezov.tuucho.core.domain.business.protocol.NavigationTransitionStackHelperProtocol
import kotlinx.serialization.json.JsonObject

class FadeNavigationTransitionStackHelper : NavigationTransitionStackHelperProtocol {

    override fun isForeground(transitionSpec: JsonObject) = false

}