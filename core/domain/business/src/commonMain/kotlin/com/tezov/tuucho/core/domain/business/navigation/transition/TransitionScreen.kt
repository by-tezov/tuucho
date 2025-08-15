package com.tezov.tuucho.core.domain.business.navigation.transition

data class TransitionScreen(
    val enter: TransitionSet,
    val exit: TransitionSet,
)