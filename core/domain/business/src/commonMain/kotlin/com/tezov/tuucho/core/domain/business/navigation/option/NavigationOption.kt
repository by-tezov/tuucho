package com.tezov.tuucho.core.domain.business.navigation.option

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute

data class NavigationOption(
    val singleTop: Boolean? = null,
    val popUpTo: PopUpTo? = null,
    val clearStack: Boolean? = null,
) {
    data class PopUpTo(val route: NavigationRoute, val inclusive: Boolean)
}