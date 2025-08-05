package com.tezov.tuucho.core.domain.business.navigation.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepositoryProtocol.Event
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol

interface NavigationStackRepositoryProtocol {

    sealed class Event {

        object Clear : Event()

        class SavedForReuse(val fromIndex: Int, val destination: NavigationDestination) : Event()

        class RemovedFromTail(val destinations: List<NavigationDestination>) : Event()

        class AddedAtTail(val destination: NavigationDestination) : Event()

        class ReuseRestoredAtTail(val destination: NavigationDestination) : Event()

    }

    suspend fun swallow(destination: NavigationDestination): List<Event>

}

class NavigationStackRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : NavigationStackRepositoryProtocol {

    private val stack = mutableListOf<NavigationDestination>()

    override suspend fun swallow(destination: NavigationDestination): List<Event> =
        coroutineScopes.onUiProcessor {
            when (val route = destination.route) {
                is NavigationRoute.Back -> {
                    stack.removeLast()
                    listOf(Event.Clear)
                }

                is NavigationRoute.Finish -> {
                    stack.clear()
                    listOf(Event.Clear)
                }

                is NavigationRoute.Url -> {
                    val events = mutableListOf<Event>()
                    val option = destination.option
                    val reusableDestination = if (option.singleTop == true) {
                        stack.indexOfLast { it.route == route }
                            .takeIf { it >= 0 }
                            ?.let { index ->
                                stack.removeAt(index).also {
                                    events.add(Event.SavedForReuse(index, it))
                                }
                            }
                    } else null

                    if (option.clearStack == true) {
                        stack.clear()
                        events.add(Event.Clear)
                    }

                    option.popUpTo?.let { popUpTo ->
                        val index = stack.indexOfLast { it.route == popUpTo.route }
                        if (index >= 0) {
                            stack
                                .subList(index + if (popUpTo.inclusive) 0 else 1, stack.size)
                                .also { events.add(Event.RemovedFromTail(it.toList())) }
                                .clear()
                        } else {
                            throw DomainException.Default("popUpTo route ${popUpTo.route} not found in stack")
                        }
                    }
                    reusableDestination?.let {
                        events.add(Event.ReuseRestoredAtTail(it))
                        stack.add(it)
                    } ?: run {
                        events.add(Event.AddedAtTail(destination))
                        stack.add(destination)
                    }
                    events.toList()
                }

                else -> throw DomainException.Default("Unknown route $route")
            }
        }

}