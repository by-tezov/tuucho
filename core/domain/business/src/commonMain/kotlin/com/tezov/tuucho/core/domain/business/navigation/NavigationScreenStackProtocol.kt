package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.Destination
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationScreenStackProtocol(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val screenRenderer: ScreenRendererProtocol,
) : NavigationRepositoryProtocol.StackScreen, KoinComponent {

    data class Item(
        val destination: NavigationDestination,
        val screen: ScreenProtocol,
    )

    private val _events = Notifier.Emitter<ScreenProtocol.IdentifierProtocol>()
    override val events get() = _events.createCollector

    private val stack = mutableListOf<Item>()

    override fun getView(identifier: SourceIdentifierProtocol): ScreenProtocol? {
        return stack.firstOrNull {
            identifier.accept(it.screen.identifier)
        }?.screen
    }

    override fun getViews(url: String): List<ScreenProtocol>? {
        return stack
            .filter { (it.destination.route as? NavigationRoute.Url)?.value == url }
            .map { it.screen }
    }

    override suspend fun swallow(
        events: List<Destination.Event>,
        componentObject: JsonObject?,
    ) {
        val reuseBin = mutableListOf<Item>()
        for (event in events) {
            when (event) {
                is Destination.Event.Clear -> clear()
                is Destination.Event.SavedForReuse -> savedForReuse(event, reuseBin)
                is Destination.Event.RemovedFromTail -> removedFromTail(event)
                is Destination.Event.AddedAtTail -> addedAtTail(
                    event,
                    componentObject ?: throw DomainException.Default("componentObject can't be null")
                )

                is Destination.Event.ReuseRestoredAtTail -> reuseRestoredAtTail(event, reuseBin)
                is Destination.Event.RemovedAtTail -> removedAtTail(event)
            }
        }
    }

    private fun clear() {
        stack.clear()
        // TODO, event finish
    }

    private fun savedForReuse(
        event: Destination.Event.SavedForReuse,
        reuseBin: MutableList<Item>,
    ) {
        val route = event.destination.route
        stack.indexOfLast { it.destination.route == route }
            .takeIf { it >= 0 }
            ?.let { stack.removeAt(it) }
            ?.let { reuseBin.add(it) }
    }

    private fun removedFromTail(
        event: Destination.Event.RemovedFromTail,
    ) {
        for (destination in event.destinations) {
            stack.asReversed()
                .indexOfLast { it.destination.route == destination.route }
                .takeIf { it >= 0 }
                ?.let { stack.removeAt(it) }
        }
    }

    private suspend fun removedAtTail(
        event: Destination.Event.RemovedAtTail,
    ) {
        val route = event.destination.route
        stack.indexOfLast { it.destination.route == route }
            .takeIf { it >= 0 }
            ?.let { stack.removeAt(it) }
            ?: throw DomainException.Default("Expected Screen for route $route not found")

        stack.lastOrNull()?.let {
            coroutineScopes.launchOnEvent {
                _events.emit(it.screen.identifier)
            }
        } ?: run {
            // TODO, event finish
        }
    }

    private suspend fun addedAtTail(
        event: Destination.Event.AddedAtTail,
        componentObject: JsonObject,
    ) {
        val screen = screenRenderer.process(componentObject)
        stack.add(
            Item(
                destination = event.destination,
                screen = screen
            )
        )
        coroutineScopes.launchOnEvent {
            _events.emit(screen.identifier)
        }
    }

    private fun reuseRestoredAtTail(
        event: Destination.Event.ReuseRestoredAtTail,
        reuseBin: MutableList<Item>,
    ) {
        val route = event.destination.route
        val item = reuseBin.indexOfLast { it.destination.route == route }
            .takeIf { it >= 0 }
            ?.let { reuseBin.removeAt(it) }
            ?: throw DomainException.Default("Expected reusable Screen for route $route not found")
        stack.add(
            item.copy(
                destination = event.destination
            )
        )
        coroutineScopes.launchOnEvent {
            _events.emit(item.screen.identifier)
        }
    }

}