package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationScreen

interface AnimationProtocol {

    fun enter(block: AnimationScreen.() -> Unit)

    fun exit(block: AnimationScreen.() -> Unit)

}