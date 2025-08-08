package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject

sealed interface NavigationRepositoryProtocol {

    interface StackRoute {

        suspend fun routes(): List<NavigationRoute>

        suspend fun forward(
            route: NavigationRoute,
            navigationOptionObject: JsonObject?,
        ): NavigationRoute?

        suspend fun backward(
            route: NavigationRoute,
        )

    }

    interface StackScreen {

        suspend fun routes(): List<NavigationRoute>

        suspend fun getScreens(routes: List<NavigationRoute>): List<ScreenProtocol>

        suspend fun getScreenOrNull(route: NavigationRoute): ScreenProtocol?

        suspend fun getScreensOrNull(url: String): List<ScreenProtocol>?

        suspend fun forward(
            route: NavigationRoute,
            componentObject: JsonObject,
        )

        suspend fun backward(
            routes: List<NavigationRoute>,
        )

    }

    interface StackTransition {

        sealed class Event {
            data class Idle(val routes: List<NavigationRoute>) : Event()
            data object PrepareTransition : Event()
            data class RequestTransition(
                val foregroundGroup: Group,
                val backgroundGroup: Group,
            ) : Event() {
                data class Group(
                    val routes: List<NavigationRoute>,
                    val transitionSpecObject: JsonObject,
                )
            }

            data object TransitionComplete : Event()
        }

        suspend fun routes(): List<NavigationRoute>

        suspend fun isBusy(): Boolean

        val events: Notifier.Collector<Event>

        suspend fun notifyTransitionCompleted()

        suspend fun forward(
            routes: List<NavigationRoute>,
            navigationExtraObject: JsonObject?,
            navigationTransitionObject: JsonObject?,
        )

        suspend fun backward(
            routes: List<NavigationRoute>,
        )
    }

}

