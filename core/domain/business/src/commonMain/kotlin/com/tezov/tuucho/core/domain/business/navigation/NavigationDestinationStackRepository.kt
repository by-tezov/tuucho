package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.Destination

class NavigationDestinationStackRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : Destination {

    private val _stack = mutableListOf<NavigationDestination>()

    override val stack = _stack as List<NavigationDestination>

    override suspend fun swallow(destination: NavigationDestination): List<Destination.Event> =
        coroutineScopes.onRenderer {
            when (val route = destination.route) {
                is NavigationRoute.Back -> {
                    val removed = _stack.removeLast()
                    listOf(Destination.Event.RemovedAtTail(removed))
                }

                is NavigationRoute.Finish -> {
                    _stack.clear()
                    listOf(Destination.Event.Clear)
                }

                is NavigationRoute.Url -> {
                    val events = mutableListOf<Destination.Event>()
                    val option = destination.option
                    val reusableDestination = if (option?.singleTop == true) {
                        _stack.indexOfLast { it.route == route }
                            .takeIf { it >= 0 }
                            ?.let { index ->
                                _stack.removeAt(index).also {
                                    events.add(Destination.Event.SavedForReuse(index, it))
                                }
                            }
                    } else null

                    if (option?.clearStack == true) {
                        _stack.clear()
                        events.add(Destination.Event.Clear)
                    }

                    option?.popUpTo?.let { popUpTo ->
                        val index = _stack.indexOfLast { it.route == popUpTo.route }
                        if (index >= 0) {
                            _stack
                                .subList(index + if (popUpTo.inclusive) 0 else 1, _stack.size)
                                .also { events.add(Destination.Event.RemovedFromTail(it.toList())) }
                                .clear()
                        } else {
                            throw DomainException.Default("popUpTo route ${popUpTo.route} not found in stack")
                        }
                    }
                    reusableDestination?.let {
                        events.add(Destination.Event.ReuseRestoredAtTail(it))
                        _stack.add(it)
                    } ?: run {
                        events.add(Destination.Event.AddedAtTail(destination))
                        _stack.add(destination)
                    }
                    events.toList()
                }
            }
        }


}