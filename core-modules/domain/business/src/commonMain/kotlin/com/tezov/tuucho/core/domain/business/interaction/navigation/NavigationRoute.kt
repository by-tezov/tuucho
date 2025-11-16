package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator.Id

sealed class NavigationRoute(
    open val id: Id,
) {
    abstract fun accept(
        other: Any
    ): Boolean

    object Back : NavigationRoute(Id("back")) {
        override fun accept(
            other: Any
        ): Boolean = other is Back

        override fun toString(): String = id.value
    }

    object Finish : NavigationRoute(Id("finish")) {
        override fun accept(
            other: Any
        ): Boolean = other is Finish

        override fun toString(): String = id.value
    }

    data class Url(
        override val id: Id,
        val value: String
    ) : NavigationRoute(id) {
        override fun accept(
            other: Any
        ): Boolean = (other is Url && other.value == value) || (other is String && other == value)
    }
}
