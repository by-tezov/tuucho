package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.priorLastOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject

internal class NavigationStackScreenRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val screenRenderer: ScreenRendererProtocol,
) : StackScreen,
    TuuchoKoinComponent {
    private val stack = mutableListOf<ScreenProtocol>()
    private val mutex = Mutex()

    override suspend fun routes() = coroutineScopes.navigation.await {
        mutex.withLock { stack.map { it.route } }
    }

    override suspend fun getScreens(
        routes: List<NavigationRoute.Url>
    ) = coroutineScopes.navigation.await {
        mutex.withLock {
            stack.filter { screen ->
                routes.any { it == screen.route }
            }
        }
    }

    override suspend fun getScreenOrNull(
        route: NavigationRoute
    ) = coroutineScopes.navigation.await {
        mutex.withLock {
            when (route) {
                is NavigationRoute.Current -> stack.lastOrNull()
                is NavigationRoute.Back -> stack.priorLastOrNull()
                else -> stack.firstOrNull { route.id == it.route.id }
            }
        }
    }

    override suspend fun getScreensOrNull(
        url: String
    ) = coroutineScopes.navigation.await {
        mutex.withLock { stack.filter { it.route.accept(url) } }
    }

    override suspend fun forward(
        route: NavigationRoute.Url,
        componentObject: JsonObject,
    ) {
        coroutineScopes.navigation.await {
            screenRenderer
                .process(
                    route = route,
                    componentObject = componentObject
                ).also {
                    mutex.withLock { stack.add(it) }
                }
        }
    }

    override suspend fun backward(
        routes: List<NavigationRoute.Url>,
    ) {
        coroutineScopes.navigation.await {
            mutex.withLock {
                stack.retainAll { screen ->
                    routes.any { it == screen.route }
                }
            }
        }
    }
}
