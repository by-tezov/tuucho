package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationStackScreenRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val screenRenderer: ScreenRendererProtocol,
) : StackScreen, KoinComponent {

    private val stack = mutableListOf<ScreenProtocol>()
    private val stackLock = Mutex()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stackLock.withLock { stack.map { it.route } }
    }

    override suspend fun getScreens(routes: List<NavigationRoute>) =
        coroutineScopes.navigation.await {
            stackLock.withLock {
                stack.filter { screen ->
                    routes.any { it == screen.route }
                }
            }
        }

    override suspend fun getScreenOrNull(route: NavigationRoute) =
        coroutineScopes.navigation.await {
            stackLock.withLock { stack.firstOrNull { route.id == it.route.id } }
        }

    override suspend fun getScreensOrNull(url: String) = coroutineScopes.navigation.await {
        stackLock.withLock { stack.filter { it.route.accept(url) } }
    }

    override suspend fun forward(
        route: NavigationRoute,
        componentObject: JsonObject,
    ) {
        coroutineScopes.navigation.await {
            val screen = screenRenderer.process(
                route = route,
                componentObject = componentObject
            )
            stackLock.withLock {
                stack.add(screen)
            }
        }
    }

    override suspend fun backward(
        routes: List<NavigationRoute>,
    ) {
        coroutineScopes.navigation.await {
            stackLock.withLock {
                stack.retainAll { screen ->
                    routes.any { it == screen.route }
                }
            }
        }
    }

}