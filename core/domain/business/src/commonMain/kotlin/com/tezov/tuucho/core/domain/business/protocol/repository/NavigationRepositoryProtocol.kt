package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.option.NavigationOption
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject

sealed interface NavigationRepositoryProtocol {

    interface StackRoute {

        suspend fun routes(): List<NavigationRoute>

        suspend fun swallow(
            route: NavigationRoute,
            option: NavigationOption,
        ): NavigationRoute?

        suspend fun spit(
            route: NavigationRoute,
        )

    }

    interface StackScreen {

        suspend fun routes(): List<NavigationRoute>

        suspend fun getScreenOrNull(route: NavigationRoute): ScreenProtocol?

        suspend fun getScreensOrNull(url: String): List<ScreenProtocol>?

        suspend fun swallow(
            route: NavigationRoute,
            componentObject: JsonObject,
        )

        suspend fun spit(
            routes: List<NavigationRoute>,
        )

    }

    interface StackAnimator {

        suspend fun routes(): List<NavigationRoute>

        val animate: Notifier.Collector<Boolean>

        suspend fun getVisibleRoutes(): List<NavigationRoute>

        suspend fun notifyTransitionCompleted()

        suspend fun swallow(
            routes: List<NavigationRoute>,
            animationObject: JsonObject,
        )

        suspend fun spit(
            routes: List<NavigationRoute>,
        )
    }

}

