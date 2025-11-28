package com.tezov.tuucho.core.domain.business.interaction.navigation

sealed class NavigationRoute(
    open val id: String,
) {
    abstract fun accept(
        other: Any
    ): Boolean

    data object Back : NavigationRoute("back") {
        override fun accept(
            other: Any
        ): Boolean = other is Back

        override fun toString(): String = id
    }

    data object Finish : NavigationRoute("finish") {
        override fun accept(
            other: Any
        ): Boolean = other is Finish

        override fun toString(): String = id
    }

    data object Current : NavigationRoute("current") {
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
