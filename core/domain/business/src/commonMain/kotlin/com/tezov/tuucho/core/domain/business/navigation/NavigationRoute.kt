package com.tezov.tuucho.core.domain.business.navigation

sealed class NavigationRoute(
    val id: String,
) {

    abstract fun accept(other: Any): Boolean

    object Back : NavigationRoute("back") {
        override fun accept(other: Any): Boolean {
            return other is Back
        }

        override fun toString(): String {
            return id
        }
    }

    object Finish : NavigationRoute("finish") {
        override fun accept(other: Any): Boolean {
            return other is Finish
        }

        override fun toString(): String {
            return id
        }
    }

    class Url(id: String, val value: String) : NavigationRoute(id) {

        override fun accept(other: Any): Boolean {
            return (other is Url && other.value == value) || (other is String && other == value)
        }

        override fun toString(): String {
            return "$value:$id"
        }
    }
}
