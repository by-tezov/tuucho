package com.tezov.tuucho.core.domain.business.navigation

sealed class NavigationRoute() {
    object Back : NavigationRoute()
    object Finish : NavigationRoute()
    data class Url(val value: String) : NavigationRoute()
}
