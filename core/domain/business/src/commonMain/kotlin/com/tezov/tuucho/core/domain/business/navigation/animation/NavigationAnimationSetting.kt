package com.tezov.tuucho.core.domain.business.navigation.animation

class NavigationAnimationSetting(
    default: NavigationAnimationType,
) {
    var push: NavigationAnimationType = default
    var pop: NavigationAnimationType = default
}