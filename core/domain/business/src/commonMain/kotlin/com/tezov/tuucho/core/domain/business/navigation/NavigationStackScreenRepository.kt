package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationStackScreenRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val screenRenderer: ScreenRendererProtocol,
) : StackScreen, KoinComponent {

    private val stack = mutableListOf<ScreenProtocol>()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stack.map { it.route }
    }

    override suspend fun getScreenOrNull(route: NavigationRoute) =
        coroutineScopes.navigation.await {
            stack.firstOrNull { route.id == it.route.id }
        }

    override suspend fun getScreensOrNull(url: String) = coroutineScopes.navigation.await {
        stack.filter { it.route.accept(url) }
    }

    override suspend fun swallow(
        route: NavigationRoute,
        componentObject: JsonObject,
    ) {
        coroutineScopes.navigation.await {
            val screen = screenRenderer.process(
                route = route,
                componentObject = componentObject
            )
            stack.add(screen)
        }
    }

    override suspend fun spit(
        routes: List<NavigationRoute>,
    ) {
        coroutineScopes.navigation.await {
            val routeIds = routes.map { it.id }.toHashSet()
            stack.removeAll { screen -> screen.route.id !in routeIds }
        }
    }

}