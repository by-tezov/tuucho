package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionNavigation
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionScreen
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.Type
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition
import com.tezov.tuucho.core.domain.business.usecase.NavigationStackTransitionHelperFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationStackTransitionRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val navigationStackTransitionHelperFactory: NavigationStackTransitionHelperFactoryUseCase,
) : StackTransition, KoinComponent {

    private var lastEvent: StackTransition.Event = StackTransition.Event.Idle(routes = emptyList())
    private val _events = Notifier.Emitter<StackTransition.Event>()
    override val events get() = _events.createCollector

    private data class Item(
        val route: NavigationRoute,
        val extraObject: JsonObject?,
        val transitionObject: JsonObject?,
    )

    private var stack = mutableListOf<Item>()
    private val stackLock = Mutex()

    override suspend fun isBusy() = coroutineScopes.event.await {
        stackLock.withLock { lastEvent !is StackTransition.Event.Idle }
    }

    override suspend fun routes() = coroutineScopes.navigation.await {
        stackLock.withLock { stack.map { it.route } }
    }

    private fun emit(event: StackTransition.Event) {
        coroutineScopes.event.async {
            stackLock.withLock {
                lastEvent = event
                _events.emit(event)
            }
        }
    }

    override suspend fun notifyTransitionCompleted() {
        stackLock.withLock {
            if (lastEvent !is StackTransition.Event.RequestTransition) {
                return
            }
        }
        emit(StackTransition.Event.TransitionComplete)
    }

    private fun Item.transitionSpec(
        directionNavigation: String,
        directionScreen: String,
    ) = transitionObject
        ?.withScope(SettingNavigationTransitionSchema::Scope)
        ?.let { it[directionNavigation] }
        ?.withScope(SettingNavigationTransitionSchema.Set::Scope)
        ?.let { it[directionScreen] as? JsonObject }
        ?: run {
            JsonNull.withScope(SettingNavigationTransitionSchema.Spec::Scope).apply {
                type = Type.none
            }.collect()
        }

    private suspend fun isForeground(spec: JsonObject?) = spec?.let {
        useCaseExecutor.invokeSuspend(
            useCase = navigationStackTransitionHelperFactory,
            input = NavigationStackTransitionHelperFactoryUseCase.Input(
                prototypeObject = spec
            )
        ).helper.isForeground(spec)
    } ?: true

    private fun Item.isBackgroundSolid() = extraObject
        ?.withScope(SettingNavigationSchema.Extra::Scope)
        ?.isBackgroundSolid ?: true

    override suspend fun forward(
        routes: List<NavigationRoute>,
        navigationExtraObject: JsonObject?,
        navigationTransitionObject: JsonObject?,
    ) {
        emit(StackTransition.Event.PrepareTransition)
        coroutineScopes.navigation.await {
            val listenerDeferred = listenEndOfTransition(routes)
            stackLock.withLock {
                // assume, only one added at tail or bring back to tail
                val pushedRoute = routes.last()
                val existingIndex = stack.indexOfFirst { it.route == pushedRoute }
                if (existingIndex >= 0) {
                    val route = stack.removeAt(existingIndex)
                    stack.add(route)
                } else {
                    stack.add(
                        Item(
                            route = pushedRoute,
                            extraObject = navigationTransitionObject,
                            transitionObject = navigationTransitionObject
                        )
                    )
                }
            }
            emit(buildTransitionEvent(DirectionNavigation.forward))
            listenerDeferred.await()
        }
    }

    override suspend fun backward(routes: List<NavigationRoute>) {
        emit(StackTransition.Event.PrepareTransition)
        coroutineScopes.navigation.await {
            val listenerDeferred = listenEndOfTransition(routes)
            emit(buildTransitionEvent(DirectionNavigation.backward))
            listenerDeferred.await()
        }
    }

    private fun listenEndOfTransition(routes: List<NavigationRoute>) =
        coroutineScopes.navigation.async {
            events.filter { it == StackTransition.Event.TransitionComplete }.once(block = {
                val event: StackTransition.Event.Idle = stackLock.withLock {
                    stack.retainAll { item ->
                        routes.any { it == item.route }
                    }
                    StackTransition.Event.Idle(
                        routes = buildList {
                            val idleItem = stack.last()
                            add(idleItem.route)
                            if (!idleItem.isBackgroundSolid()) {
                                for (item in stack.dropLast(1).asReversed()) {
                                    add(item.route)
                                    if (item.isBackgroundSolid()) break
                                }
                            }
                        }
                    )
                }
                emit(event)
            })
        }

    private suspend fun buildTransitionEvent(
        directionNavigation: String,
    ): StackTransition.Event.RequestTransition = stackLock.withLock {
        val isForward = directionNavigation == DirectionNavigation.forward

        val lastItem = stack.last()
        val enteringTransitionSpec = lastItem.transitionSpec(
            directionNavigation = directionNavigation,
            directionScreen = DirectionScreen.enter
        )
        val exitingTransitionSpec = lastItem.transitionSpec(
            directionNavigation = directionNavigation,
            directionScreen = DirectionScreen.exit
        )

        val lastItemGroup = StackTransition.Event.RequestTransition.Group(
            routes = listOf(lastItem.route),
            transitionSpecObject = if (isForward) enteringTransitionSpec else exitingTransitionSpec
        )

        val priorItemGroup = StackTransition.Event.RequestTransition.Group(
            routes = buildList {
                val priorItem = stack.dropLast(1).lastOrNull()
                priorItem?.let {
                    add(it.route)
                    if (!it.isBackgroundSolid()) {
                        for (item in stack.dropLast(2).asReversed()) {
                            add(item.route)
                            if (item.isBackgroundSolid()) break
                        }
                    }
                }
            },
            transitionSpecObject = if (!isForward) enteringTransitionSpec else exitingTransitionSpec
        )

        val isForegroundGroup =
            isForeground(if (isForward) enteringTransitionSpec else exitingTransitionSpec)
        StackTransition.Event.RequestTransition(
            foregroundGroup = if (isForegroundGroup) lastItemGroup else priorItemGroup,
            backgroundGroup = if (!isForegroundGroup) lastItemGroup else priorItemGroup,
        )
    }

}