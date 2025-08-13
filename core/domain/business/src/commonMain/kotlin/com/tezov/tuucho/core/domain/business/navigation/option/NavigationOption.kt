package com.tezov.tuucho.core.domain.business.navigation.option

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute

data class NavigationOption(
    val single: Boolean,
    val reuse: String?,
    val popUpTo: PopUpTo?,
    val clearStack: Boolean,
) {
    data class PopUpTo(
        val route: NavigationRoute,
        val inclusive: Boolean,
        val greedy: Boolean,
    )
}