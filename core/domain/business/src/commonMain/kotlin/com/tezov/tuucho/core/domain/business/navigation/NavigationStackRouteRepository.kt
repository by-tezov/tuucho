package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationOptionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingOptionSelector
import com.tezov.tuucho.core.domain.business.navigation.selector.PageBreadCrumbNavigationOptionSelector
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationOptionSelectorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackRoute
import com.tezov.tuucho.core.domain.business.usecase.SettingOptionSelectorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

class NavigationStackRouteRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val settingOptionSelectorFactory: SettingOptionSelectorFactoryUseCase,
) : StackRoute {

    private val stack = mutableListOf<NavigationRoute>()
    private val stackLock = Mutex()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stackLock.withLock { stack.toList() }
    }

    override suspend fun push(
        route: NavigationRoute,
        navigationOptionObject: JsonArray?,
    ) = coroutineScopes.navigation.await {
        val option = navigationOptionObject.navigationOptionResolver()
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

    override suspend fun pop(route: NavigationRoute) {
        coroutineScopes.navigation.await {
            stackLock.withLock {
                when (route) {
                    is NavigationRoute.Back -> navigateBack()
                    is NavigationRoute.Finish -> navigateFinish()
                    else -> throw DomainException.Default("Route $route can't be spitted, maybe you should use swallow")
                }
                return@await null
            }
        }
    }

    private fun navigateBack() {
        stack.removeLastOrNull()
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
            when (reuse) {
                SettingNavigationOptionSchema.Value.Reuse.last -> {
                    stack
                        .indexOfLast { it.accept(route) }
                        .takeIf { it >= 0 }
                        ?.let { index -> stack.removeAt(index) }
                }

                SettingNavigationOptionSchema.Value.Reuse.first -> {
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

    private suspend fun JsonArray?.navigationOptionResolver() = this
        ?.firstOrNull { it.accept() }
        ?.let { NavigationOption.from(it.jsonObject) }
        ?: NavigationOption(
            single = false,
            reuse = null,
            popUpTo = null,
            clearStack = false
        )

    private suspend fun JsonElement.accept(): Boolean {
        val selector = withScope(SettingOptionSelector::Scope).self ?: return true
        return useCaseExecutor.invokeSuspend(
            useCase = settingOptionSelectorFactory,
            input = SettingOptionSelectorFactoryUseCase.Input(
                prototypeObject = selector
            )
        ).selector.accept()
    }

    private fun NavigationOptionSelectorProtocol.accept() = when (this) {
        is PageBreadCrumbNavigationOptionSelector -> accept(
            stack.mapNotNull { (it as? NavigationRoute.Url)?.value }
        )

        else -> throw DomainException.Default("Unknown navigation option selector $this")
    }

}