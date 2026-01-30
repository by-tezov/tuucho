package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition.LocalDestination

sealed class NavigationRoute(
    open val id: String,
) {
    abstract fun accept(
        other: Any
    ): Boolean

    data object Back : NavigationRoute(LocalDestination.Target.back) {
        override fun accept(
            other: Any
        ): Boolean = other is Back

        override fun toString(): String = id
    }

    data object Finish : NavigationRoute(LocalDestination.Target.finish) {
        override fun accept(
            other: Any
        ): Boolean = other is Finish

        override fun toString(): String = id
    }

    data object Current : NavigationRoute(LocalDestination.Target.current) {
        override fun accept(
            other: Any
        ): Boolean = other is Current

        override fun toString(): String = id
    }

    data class Url(
        override val id: String,
        val value: String
    ) : NavigationRoute(id) {
        override fun accept(
            other: Any
        ): Boolean = (other is Url && other.value == value) || (other is String && other == value)

        override fun toString(): String = value
    }
}
