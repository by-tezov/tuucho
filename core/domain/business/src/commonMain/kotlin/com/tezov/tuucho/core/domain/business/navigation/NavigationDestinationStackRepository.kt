package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.Event
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackDestination

class NavigationDestinationStackRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : StackDestination {

    private val _stack = mutableListOf<NavigationDestination>()

    override suspend fun stack() = coroutineScopes.navigation.on {
        _stack.toList()
    }

    override suspend fun swallow(destination: NavigationDestination) =
        coroutineScopes.navigation.on {
            when (destination.route) {
                is NavigationRoute.Back -> navigateBack()
                is NavigationRoute.Finish -> navigateFinish()
                is NavigationRoute.Url -> navigateUrl(destination)
            }
        }

    private fun navigateBack(): List<Event<NavigationRoute>> {
        val removed = _stack.removeLast()
        return listOf(Event.RemovedAtTail(removed.route))
    }

    private fun navigateFinish(): List<Event<NavigationRoute>> {
        _stack.clear()
        return listOf(Event.Clear)
    }

    private fun navigateUrl(
        destination: NavigationDestination,
    ): List<Event<NavigationRoute>> {
        val route = (destination.route as NavigationRoute.Url)
        val events = mutableListOf<Event<NavigationRoute>>()
        val option = destination.option
        val reusableDestination = if (option?.singleTop == true) {
            _stack.indexOfLast { it.route == route }
                .takeIf { it >= 0 }
                ?.let { index ->
                    _stack.removeAt(index).also {
                        events.add(Event.SavedForReuse(index, it.route))
                    }
                }
        } else null

        if (option?.clearStack == true) {
            _stack.clear()
            events.add(Event.Clear)
        }

        option?.popUpTo?.let { popUpTo ->
            val index = _stack.indexOfLast { it.route == popUpTo.route }
            if (index >= 0) {
                val subList = _stack.subList(index + if (popUpTo.inclusive) 0 else 1, _stack.size)
                events.add(Event.RemovedFromTail(subList.map { it.route }))
                subList.clear()
            } else {
                throw DomainException.Default("popUpTo route ${popUpTo.route} not found in stack")
            }
        }
        reusableDestination?.let {
            events.add(Event.ReuseRestoredAtTail(it.route))
            _stack.add(it)
        } ?: run {
            events.add(Event.AddedAtTail(destination.route))
            _stack.add(destination)
        }
        return events.toList()
    }


}