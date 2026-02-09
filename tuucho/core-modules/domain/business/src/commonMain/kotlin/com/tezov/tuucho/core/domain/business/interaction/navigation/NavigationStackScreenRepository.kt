package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.priorLastOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class NavigationStackScreenRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val materialCacheRepository: NavigationRepositoryProtocol.MaterialCache,
    private val screenFactory: ScreenFactoryProtocol,
) : StackScreen,
    TuuchoKoinComponent {
    private var stack = mutableListOf<ScreenProtocol>()
    private val mutex = Mutex()

    override suspend fun routes() = coroutineScopes.default.withContext {
        mutex.withLock { stack.map { it.route } }
    }

    override suspend fun getScreens(
        routes: List<NavigationRoute.Url>
    ) = coroutineScopes.default.withContext {
        mutex.withLock {
            stack.filter { screen ->
                routes.any { it == screen.route }
            }
        }
    }

    override suspend fun getScreenOrNull(
        route: NavigationRoute
    ) = coroutineScopes.default.withContext {
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
    ) = coroutineScopes.default.withContext {
        mutex.withLock { stack.filter { it.route.accept(url) } }
    }

    override suspend fun forward(
        route: NavigationRoute.Url
    ) {
        val componentObject = materialCacheRepository.getComponentObject(route.value)
        coroutineScopes.default.withContext {
            screenFactory
                .create(
                    route = route,
                    componentObject = componentObject
                ).also {
                    mutex.withLock { stack.add(it) }
                }
        }
    }

    override suspend fun backward() {
        val routes = navigationStackRouteRepository.routes()
        coroutineScopes.default.withContext {
            mutex.withLock {
                val iterator = stack.listIterator()
                while (iterator.hasNext()) {
                    val screen = iterator.next()
                    if (routes.none { it == screen.route }) {
                        iterator.remove()
                        materialCacheRepository.release(screen.route.value)
                    }
                }
            }
        }
    }
}
