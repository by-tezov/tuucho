package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationScreen
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

sealed interface NavigationRepositoryProtocol {

    interface StackRoute {

        suspend fun routes(): List<NavigationRoute>

        suspend fun push(
            route: NavigationRoute,
            navigationOptionObject: JsonArray?,
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
            data object RequestTransitionForward: Event()
            data object RequestTransitionBackward: Event()
            data object TransitionComplete: Event()
            data object Idle: Event()
        }

        data class Item(
            val route: NavigationRoute,
            val animationScreen: AnimationScreen,
        )

        suspend fun routes(): List<NavigationRoute>

        suspend fun isBusy(): Boolean

        val events: Notifier.Collector<Event>

        suspend fun getRoutesWithAnimationOptions(): List<Item>

        suspend fun notifyTransitionCompleted()

        suspend fun swallow(
            routes: List<NavigationRoute>,
            animationOptionObject: JsonArray?,
        )

        suspend fun spit(
            routes: List<NavigationRoute>,
        )
    }

}

