package com.tezov.tuucho.core.domain.business.navigation.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.ViewContext
import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepositoryProtocol.Event
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.RenderViewContextUseCase
import com.tezov.tuucho.core.domain.tool.async.Notifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ViewContextStackRepositoryProtocol {

    val events: Notifier.Collector<NavigationRoute>

    val currentViewContext: ViewContext?

    fun getViewState(url: String): StateViewProtocol

    suspend fun swallow(events: List<Event>)

}

class ViewContextStackRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : ViewContextStackRepositoryProtocol, KoinComponent {

    private val _events = Notifier.Emitter<NavigationRoute>()
    override val events get() = _events.createCollector

    //TODO
    private val renderViewContext: RenderViewContextUseCase by inject()
    private val retrieveMaterialRepository: RetrieveMaterialRepositoryProtocol by inject()

    override val currentViewContext: ViewContext?
        get() = stack.entries.lastOrNull()?.value //TODO

    private val stack = LinkedHashMap<NavigationRoute, ViewContext>()

    override fun getViewState(url: String) = stack.entries.first {
        (it.key as NavigationRoute.Url).value == url
    }.value.state

    override suspend fun swallow(events: List<Event>) {
        val reuseBin = mutableMapOf<NavigationRoute, ViewContext>()
        for (event in events) {
            when (event) {
                is Event.Clear -> clear()
                is Event.SavedForReuse -> savedForReuse(event, reuseBin)
                is Event.RemovedFromTail -> removedFromTail(event)
                is Event.AddedAtTail -> addedAtTail(event)
                is Event.ReuseRestoredAtTail -> reuseRestoredAtTail(event, reuseBin)
            }
        }
    }

    private fun clear() {
        stack.clear()
    }

    private fun savedForReuse(
        event: Event.SavedForReuse,
        reuseBin: MutableMap<NavigationRoute, ViewContext>,
    ) {
        val route = event.destination.route
        stack.remove(route)?.let { reuseBin[route] = it }
    }

    private fun removedFromTail(
        event: Event.RemovedFromTail,
    ) {
        for (destination in event.destinations) {
            stack.remove(destination.route)
        }
    }

    private suspend fun addedAtTail(
        event: Event.AddedAtTail,
    ) {
        val route = event.destination.route
        (route as? NavigationRoute.Url)?.value
            ?: throw DomainException.Default("Only Url routes are supported")

        retrieveMaterialRepository.process(route.value)

//        val context = renderViewContext.invoke(url, component)
//        stack[route] = context //TODO

//        useCaseExecutor.invoke(
//            useCase = actionHandler,
//            input = ActionHandlerUseCase.Input(
//                url = url,
//                id = id,
//                action = ActionModelDomain.from(
//                    command = Action.Form.Update.command,
//                    authority = Action.Form.Update.Authority.error,
//                    target = null
//                ),
//                paramElement = results
//            ),
//        )

        println("ici")

        coroutineScopes.launchOnEvent {
            _events.emit(route)
        }
    }

    private fun reuseRestoredAtTail(
        event: Event.ReuseRestoredAtTail,
        reuseBin: MutableMap<NavigationRoute, ViewContext>,
    ) {
        val route = event.destination.route
        val context = reuseBin.remove(route)
            ?: throw DomainException.Default("Expected reusable ViewContext for route $route not found")
        stack[route] = context

        coroutineScopes.launchOnEvent {
            _events.emit(route)
        }
    }


}
