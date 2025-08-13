package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationOptionSchema
import com.tezov.tuucho.core.domain.business.navigation.option.NavigationOption
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackRoute

class NavigationStackRouteRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : StackRoute {

    private val stack = mutableListOf<NavigationRoute>()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stack.toList()
    }

    override suspend fun swallow(
        route: NavigationRoute,
        option: NavigationOption,
    ) = coroutineScopes.navigation.await {
        when (route) {
            is NavigationRoute.Url -> navigateUrl(route, option)?.let {
                return@await it
            }

            else -> throw DomainException.Default("Route $route can't be swallowed, maybe you should use spit")
        }
        return@await null
    }

    override suspend fun spit(route: NavigationRoute) {
        coroutineScopes.navigation.await {
            when (route) {
                is NavigationRoute.Back -> navigateBack()
                is NavigationRoute.Finish -> navigateFinish()
                else -> throw DomainException.Default("Route $route can't be spitted, maybe you should use swallow")
            }
            return@await null
        }
    }

    private fun navigateBack() {
        if (stack.size <= 1) {
            navigateFinish()
        } else {
            stack.removeLast()
        }
    }

    private fun navigateFinish() {
        stack.clear()
    }

    private fun navigateUrl(
        route: NavigationRoute,
        option: NavigationOption?,
    ): NavigationRoute? {
        val route = (route as NavigationRoute.Url)
        val reusableRoute = option?.reuse?.let { reuse ->
            when {
                reuse.toBooleanStrictOrNull() == true || reuse == SettingNavigationOptionSchema.Value.Reuse.last -> {
                    stack
                        .indexOfLast { it.accept(route) }
                        .takeIf { it >= 0 }
                        ?.let { index -> stack.removeAt(index) }
                }

                reuse == SettingNavigationOptionSchema.Value.Reuse.first -> {
                    stack
                        .indexOfFirst { it.accept(route) }
                        .takeIf { it >= 0 }
                        ?.let { index -> stack.removeAt(index) }
                }

                else -> null
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