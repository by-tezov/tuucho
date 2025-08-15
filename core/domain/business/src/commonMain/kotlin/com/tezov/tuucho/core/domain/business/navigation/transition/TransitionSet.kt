package com.tezov.tuucho.core.domain.business.navigation.transition

import com.tezov.tuucho.core.domain.business.navigation.transition.spec.TransitionSpec

data class TransitionSet(
    val push: TransitionSpec,
    val pop: TransitionSpec,
)