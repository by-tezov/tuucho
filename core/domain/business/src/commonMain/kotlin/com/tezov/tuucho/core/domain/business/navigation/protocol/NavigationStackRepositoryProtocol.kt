package com.tezov.tuucho.core.domain.business.navigation.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol

interface NavigationStackRepositoryProtocol {

    suspend fun swallow(destination: NavigationDestination): List<NavigationDestination>

}

class NavigationStackRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : NavigationStackRepositoryProtocol {

    private val stack = mutableListOf<NavigationDestination>()

    override suspend fun swallow(destination: NavigationDestination) =
        coroutineScopes.onUiProcessor {
            when (val route = destination.route) {
                is NavigationRoute.Back -> {
                    stack.removeLast()
                }

                is NavigationRoute.Finish -> {
                    stack.clear()
                }

                is NavigationRoute.Url -> {
                    val option = destination.option

                    val reusableDestination = if (option.singleTop == true) {
                        stack.lastOrNull { it.route == route }
                    } else null

                    if (option.clearStack == true) {
                        stack.clear()
                    }

                    option.popUpTo?.let { popUpTo ->
                        val index = stack.indexOfLast { it.route == popUpTo.route }
                        if (index >= 0) {
                            val inclusiveOffset = if (popUpTo.inclusive) 0 else 1
                            val toRemove = stack.size - (index + inclusiveOffset)
                            repeat(toRemove) {
                                stack.removeLast()
                            }
                        } else {
                            throw DomainException.Default("popUpTo route ${popUpTo.route} not found in stack")
                        }
                    }

                    val finalDestination = reusableDestination ?: destination
                    stack.removeAll { it.route == finalDestination.route }
                    stack.add(finalDestination)
                }
            }
        }.let { stack }

}