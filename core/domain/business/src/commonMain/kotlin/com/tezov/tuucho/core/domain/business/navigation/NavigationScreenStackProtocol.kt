package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.Event
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationScreenStackProtocol(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val screenRenderer: ScreenRendererProtocol,
) : StackScreen, KoinComponent {

    data class Item(
        val route: NavigationRoute,
        val screen: ScreenProtocol,
    )

    private val stack = mutableListOf<Item>()

    override suspend fun getScreen(identifier: SourceIdentifierProtocol) =
        coroutineScopes.navigation.on {
            stack.firstOrNull {
                identifier.accept(it.screen.identifier)
            }?.screen
        }

    override suspend fun getScreens(url: String) = coroutineScopes.navigation.on {
        stack
            .filter { (it.route as? NavigationRoute.Url)?.value == url }
            .map { it.screen }
    }

    override suspend fun swallow(
        events: List<Event<NavigationRoute>>,
        componentObject: JsonObject?,
    ) = coroutineScopes.navigation.on {
        val outputEvents = mutableListOf<Event<ScreenProtocol.IdentifierProtocol>>()
        val reuseBin = mutableListOf<Item>()
        for (event in events) {
            when (event) {
                is Event.Clear -> clear()
                is Event.SavedForReuse -> savedForReuse(event, reuseBin)
                is Event.RemovedFromTail -> removedFromTail(event)
                is Event.ReuseRestoredAtTail -> reuseRestoredAtTail(event, reuseBin)
                is Event.RemovedAtTail -> removedAtTail(event)
                is Event.AddedAtTail -> addedAtTail(
                    event, componentObject
                        ?: throw DomainException.Default("componentObject can't be null")
                )
            }.also { outputEvents.add(it) }
        }
        outputEvents
    }

    private fun clear(): Event<Nothing> {
        stack.clear()
        return Event.Clear
    }

    private fun savedForReuse(
        event: Event.SavedForReuse<NavigationRoute>,
        reuseBin: MutableList<Item>,
    ): Event<ScreenProtocol.IdentifierProtocol> {
        val route = event.element
        return stack.indexOfLast { it.route == route }
            .also {
                if (it != event.fromIndex) {
                    throw DomainException.Default("Inconsistent stack, found index $it, expected ${event.fromIndex}")
                }
            }
            .let { stack.removeAt(it) }
            .also { reuseBin.add(it) }
            .let {
                Event.SavedForReuse(fromIndex = event.fromIndex, element = it.screen.identifier)
            }
    }

    private fun removedFromTail(
        event: Event.RemovedFromTail<NavigationRoute>,
    ): Event<ScreenProtocol.IdentifierProtocol> {
        event.elements.firstOrNull()?.let { firstRouteToRemoved ->
            val index = stack
                .indexOfLast { it.route == firstRouteToRemoved }
            if ((stack.size - index) != event.elements.size) {
                throw DomainException.Default("Inconsistent stack, size to remove is ${stack.size - index}, expected to remove ${event.elements.size} elements")
            }
            val subList = stack.subList(index, stack.size)
            val event = Event.RemovedFromTail(subList.map { it.screen.identifier })
            subList.clear()
            return event
        }
        return Event.RemovedFromTail(emptyList())
    }

    private fun removedAtTail(
        event: Event.RemovedAtTail<NavigationRoute>,
    ): Event<ScreenProtocol.IdentifierProtocol> {
        val route = event.element
        return stack.indexOfLast { it.route == route }
            .also {
                if (it != stack.lastIndex) {
                    throw DomainException.Default("Inconsistent stack, found index $it, expected ${stack.lastIndex}")
                }
            }
            .let { stack.removeAt(it) }
            .let {
                Event.RemovedAtTail(element = it.screen.identifier)
            }
    }

    private suspend fun addedAtTail(
        event: Event.AddedAtTail<NavigationRoute>,
        componentObject: JsonObject,
    ): Event<ScreenProtocol.IdentifierProtocol> {
        val screen = screenRenderer.process(componentObject)
        stack.add(
            Item(route = event.element, screen = screen)
        )
        return Event.AddedAtTail(
            element = screen.identifier
        )
    }

    private fun reuseRestoredAtTail(
        event: Event.ReuseRestoredAtTail<NavigationRoute>,
        reuseBin: MutableList<Item>,
    ): Event<ScreenProtocol.IdentifierProtocol> {
        val route = event.element
        val item = reuseBin.indexOfLast { it.route == route }
            .takeIf { it >= 0 }
            ?.let { reuseBin.removeAt(it) }
            ?: throw DomainException.Default("Expected reusable screen for route $route not found")
        stack.add(
            item.copy(route = event.element)
        )
        return Event.ReuseRestoredAtTail(
            element = item.screen.identifier
        )
    }

}