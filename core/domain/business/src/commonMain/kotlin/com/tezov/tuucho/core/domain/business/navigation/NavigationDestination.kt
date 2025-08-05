package com.tezov.tuucho.core.domain.business.navigation

data class NavigationDestination(
    val route: NavigationRoute,
    val option: NavigationOption? = null,
)