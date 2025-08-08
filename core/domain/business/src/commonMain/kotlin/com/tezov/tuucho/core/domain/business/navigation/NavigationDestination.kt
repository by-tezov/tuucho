package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.navigation.option.NavigationOption

data class NavigationDestination(
    val route: NavigationRoute,
    val option: NavigationOption? = null,
)