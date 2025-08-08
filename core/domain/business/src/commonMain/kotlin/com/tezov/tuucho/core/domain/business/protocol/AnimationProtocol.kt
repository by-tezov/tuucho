package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.navigation.animation.NavigationAnimationSetting

interface AnimationProtocol {

    fun enter(block: NavigationAnimationSetting.() -> Unit)

    fun exit(block: NavigationAnimationSetting.() -> Unit)

}