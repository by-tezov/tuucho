package com.tezov.tuucho.core.domain.business.navigation.transitionOption

import com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting.AnimationSetting

data class AnimationType(
    val push: AnimationSetting,
    val pop: AnimationSetting,
)