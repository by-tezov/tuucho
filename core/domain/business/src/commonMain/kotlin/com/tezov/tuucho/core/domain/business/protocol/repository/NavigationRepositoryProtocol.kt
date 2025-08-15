package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionDirection
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject

sealed interface NavigationRepositoryProtocol {

    interface StackRoute {

        suspend fun routes(): List<NavigationRoute>

        suspend fun push(
            route: NavigationRoute,
            navigationOptionObject: JsonObject?,
        ): NavigationRoute?

        suspend fun pop(
            route: NavigationRoute,
        )

    }

    interface StackScreen {

        suspend fun routes(): List<NavigationRoute>

        suspend fun getScreenOrNull(route: NavigationRoute): ScreenProtocol?

        suspend fun getScreensOrNull(url: String): List<ScreenProtocol>?

        suspend fun push(
            route: NavigationRoute,
            componentObject: JsonObject,
        )

        suspend fun intersect(
            routes: List<NavigationRoute>,
        )

    }

    interface StackTransition {

        sealed class Event {
            data object Idle : Event()
            data object PrepareTransition : Event()
            data class RequestTransition(
                val directionNavigation: TransitionDirection.Navigation,
                val foregroundRoutes: List<NavigationRoute>,
                val foregroundDirectionScreen: TransitionDirection.Screen,
                val foregroundTransitionSpec: JsonObject?,
                val backgroundRoutes: List<NavigationRoute>,
                val backgroundDirectionScreen: TransitionDirection.Screen,
                val backgroundTransitionSpec: JsonObject?,
            ) : Event()

            data object TransitionComplete : Event()
        }

        suspend fun routes(): List<NavigationRoute>

        suspend fun isBusy(): Boolean

        val events: Notifier.Collector<Event>

        suspend fun notifyTransitionCompleted()

        suspend fun swallow(
            routes: List<NavigationRoute>,
            navigationTransitionScreenObject: JsonObject?,
        )

        suspend fun spit(
            routes: List<NavigationRoute>,
        )
    }

}

