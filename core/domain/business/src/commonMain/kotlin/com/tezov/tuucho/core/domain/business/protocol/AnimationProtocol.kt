package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionScreen

interface AnimationProtocol {

    fun enter(block: TransitionScreen.() -> Unit)

    fun exit(block: TransitionScreen.() -> Unit)

}