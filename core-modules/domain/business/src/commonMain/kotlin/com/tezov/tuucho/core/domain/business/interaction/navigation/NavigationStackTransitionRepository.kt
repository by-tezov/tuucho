package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.DirectionNavigation
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.DirectionScreen
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.Type
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationStackTransitionHelperFactoryUseCase
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

internal class NavigationStackTransitionRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val navigationStackTransitionHelperFactory: NavigationStackTransitionHelperFactoryUseCase,
) : StackTransition,
    TuuchoKoinComponent {
    private var lastEvent: StackTransition.Event = StackTransition.Event.Idle(routes = emptyList())
    private val _events = Notifier.Emitter<StackTransition.Event>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events get() = _events.createCollector

    private data class Item(
        val route: NavigationRoute.Url,
        val extraObject: JsonObject?,
        val transitionObject: JsonObject?,
    )

    private var stack = mutableListOf<Item>()
    private val stackLock = Mutex()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stackLock.withLock { stack.map { it.route } }
    }

    private fun emit(
        event: StackTransition.Event
    ) {
        coroutineScopes.event.async {
            stackLock.withLock {
                lastEvent = event
                _events.emit(event)
            }
        }
    }

    override suspend fun notifyTransitionCompleted() {
        emit(StackTransition.Event.TransitionComplete)
    }

    private fun Item.transitionSpec(
        directionNavigation: String,
        directionScreen: String,
    ) = transitionObject
        ?.withScope(SettingComponentNavigationTransitionSchema::Scope)
        ?.let { it[directionNavigation] }
        ?.withScope(SettingComponentNavigationTransitionSchema.Set::Scope)
        ?.let { it[directionScreen] as? JsonObject }
        ?: run {
            JsonNull
                .withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
                .apply {
                    type = Type.none
                }.collect()
        }

    private suspend fun isForeground(
        spec: JsonObject?
    ) = spec?.let {
        useCaseExecutor
            .await(
                useCase = navigationStackTransitionHelperFactory,
                input = NavigationStackTransitionHelperFactoryUseCase.Input(
                    prototypeObject = spec
                )
            ).helper
            .isForeground(spec)
    } ?: true

    private fun Item.isBackgroundSolid() = extraObject
        ?.withScope(ComponentSettingNavigationSchema.Extra::Scope)
        ?.isBackgroundSolid ?: true

    override suspend fun forward(
        routes: List<NavigationRoute.Url>,
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
                            extraObject = navigationExtraObject,
                            transitionObject = navigationTransitionObject
                        )
                    )
                }
            }
            emit(buildTransitionEvent(DirectionNavigation.forward))
            listenerDeferred.await()
        }
    }

    override suspend fun backward(
        routes: List<NavigationRoute.Url>
    ) {
        emit(StackTransition.Event.PrepareTransition)
        coroutineScopes.navigation.await {
            val listenerDeferred = listenEndOfTransition(routes)
            emit(buildTransitionEvent(DirectionNavigation.backward))
            listenerDeferred.await()
        }
    }

    private fun listenEndOfTransition(
        routes: List<NavigationRoute>
    ) = coroutineScopes.navigation.async {
        events
            .filter { it == StackTransition.Event.TransitionComplete }
            .once(block = {
                val event: StackTransition.Event.Idle = stackLock.withLock {
                    stack.retainAll { item ->
                        routes.any { it == item.route }
                    }
                    StackTransition.Event.Idle(
                        routes = buildList {
                            val lastItem = stack.last()
                            add(lastItem.route)
                            if (!lastItem.isBackgroundSolid()) {
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
