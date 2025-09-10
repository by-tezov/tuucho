package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationOptionSchema.Value.Reuse
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackRoute
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject

class NavigationStackRouteRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : StackRoute {

    private val stack = mutableListOf<NavigationRoute>()
    private val stackLock = Mutex()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stackLock.withLock { stack.toList() }
    }

    override suspend fun forward(
        route: NavigationRoute,
        navigationOptionObject: JsonObject?,
    ) = coroutineScopes.navigation.await {
        val option = navigationOptionObject?.let {
            NavigationOption.from(it)
        } ?: NavigationOption(
            single = false,
            reuse = null,
            popUpTo = null,
            clearStack = false
        )
        stackLock.withLock {
            when (route) {
                is NavigationRoute.Url -> navigateUrl(route, option)?.let {
                    return@await it
                }

                else -> throw DomainException.Default("Route $route can't be swallowed, maybe you should use spit")
            }
            return@await null
        }
    }

    override suspend fun backward(route: NavigationRoute) = coroutineScopes.navigation.await {
        stackLock.withLock {
            when (route) {
                is NavigationRoute.Back -> navigateBack()
                is NavigationRoute.Finish -> navigateFinish()
                else -> throw DomainException.Default("Route $route can't be spitted, maybe you should use swallow")
            }
        }
    }

    private fun navigateBack(): NavigationRoute? {
        stack.removeLastOrNull()
        return stack.lastOrNull()
    }

    private fun navigateFinish(): NavigationRoute? {
        stack.clear()
        return null
    }

    private fun navigateUrl(
        route: NavigationRoute,
        option: NavigationOption?,
    ): NavigationRoute? {
        val route = (route as NavigationRoute.Url)
        val reusableRoute = option?.reuse?.let { reuse ->
            when (reuse) {
                Reuse.last -> {
                    stack
                        .indexOfLast { it.accept(route) }
                        .takeIf { it >= 0 }
                        ?.let { index -> stack.removeAt(index) }
                }

                Reuse.first -> {
                    stack
                        .indexOfFirst { it.accept(route) }
                        .takeIf { it >= 0 }
                        ?.let { index -> stack.removeAt(index) }
                }

                else -> throw DomainException.Default("Invalid reuse value $reuse")
            }
        }
        if (option?.single == true) {
            stack.removeAll { it.accept(route) }
        }
        if (option?.clearStack == true) {
            stack.clear()
        }
        option?.popUpTo?.let { popUpTo ->
            val index = if (popUpTo.greedy) {
                stack.indexOfFirst { it.accept(popUpTo.route) }
            } else {
                stack.indexOfLast { it.accept(popUpTo.route) }
            }
            if (index >= 0) {
                val subList = stack.subList(index + if (popUpTo.inclusive) 0 else 1, stack.size)
                subList.clear()
            } else {
                throw DomainException.Default("popUpTo route ${popUpTo.route} not found in stack")
            }
        }
        if (reusableRoute != null) {
            stack.add(reusableRoute)
            return null
        } else {
            stack.add(route)
            return route
        }
    }
}