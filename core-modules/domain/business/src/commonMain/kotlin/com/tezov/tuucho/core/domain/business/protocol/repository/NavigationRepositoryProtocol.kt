package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject

object NavigationRepositoryProtocol {
    interface StackRoute {
        suspend fun currentRoute(): NavigationRoute.Url?

        suspend fun priorRoute(): NavigationRoute.Url?

        suspend fun routes(): List<NavigationRoute.Url>

        suspend fun forward(
            route: NavigationRoute.Url,
            navigationOptionObject: JsonObject?,
        ): NavigationRoute.Url?

        suspend fun backward(
            route: NavigationRoute,
        ): NavigationRoute.Url?
    }

    interface StackScreen {
        suspend fun routes(): List<NavigationRoute.Url>

        suspend fun getScreens(
            routes: List<NavigationRoute.Url>
        ): List<ScreenProtocol>

        suspend fun getScreenOrNull(
            route: NavigationRoute
        ): ScreenProtocol?

        suspend fun getScreensOrNull(
            url: String
        ): List<ScreenProtocol>?

        suspend fun forward(
            route: NavigationRoute.Url,
            componentObject: JsonObject,
        )

        suspend fun backward(
            routes: List<NavigationRoute.Url>,
        )
    }

    interface StackTransition {
        sealed class Event {
            data class Idle(
                val routes: List<NavigationRoute.Url>
            ) : Event()

            data object PrepareTransition : Event()

            data class RequestTransition(
                val foregroundGroup: Group,
                val backgroundGroup: Group,
            ) : Event() {
                data class Group(
                    val routes: List<NavigationRoute.Url>,
                    val transitionSpecObject: JsonObject,
                )
            }

            data object TransitionComplete : Event()
        }

        suspend fun routes(): List<NavigationRoute.Url>

        val events: Notifier.Collector<Event>

        suspend fun notifyTransitionCompleted()

        suspend fun forward(
            routes: List<NavigationRoute.Url>,
            navigationExtraObject: JsonObject?,
            navigationTransitionObject: JsonObject?,
        )

        suspend fun backward(
            routes: List<NavigationRoute.Url>,
        )
    }
}
