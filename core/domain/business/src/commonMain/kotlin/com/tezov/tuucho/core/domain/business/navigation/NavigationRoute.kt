package com.tezov.tuucho.core.domain.business.navigation

open class NavigationRoute {
    class Back() : NavigationRoute()
    class Finish() : NavigationRoute()
    class Url(val value: String) : NavigationRoute()
}
