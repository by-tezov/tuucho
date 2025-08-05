package com.tezov.tuucho.core.domain.business.navigation

open class NavigationRoute {
    object Back : NavigationRoute()
    object Finish : NavigationRoute()
    data class Url(val value: String) : NavigationRoute()
}
