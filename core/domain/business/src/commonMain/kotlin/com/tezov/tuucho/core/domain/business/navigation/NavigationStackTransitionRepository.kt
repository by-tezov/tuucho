package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition
import com.tezov.tuucho.core.domain.business.usecase.NavigationTransitionSettingFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationStackTransitionRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val navigationTransitionSettingFactory: NavigationTransitionSettingFactoryUseCase,
) : StackTransition, KoinComponent {

    private var lastEvent: StackTransition.Event = StackTransition.Event.Idle
    private val _events = Notifier.Emitter<StackTransition.Event>()
    override val events get() = _events.createCollector

    private data class Item(
        val route: NavigationRoute,
        val transitionScreenObject: JsonObject?,
    )

    private var stack = mutableListOf<Item>()
    private val stackLock = Mutex()

    override suspend fun isBusy() = coroutineScopes.event.await {
        stackLock.withLock { lastEvent != StackTransition.Event.Idle }
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
        emit(StackTransition.Event.TransitionComplete)
    }

    override suspend fun swallow(
        routes: List<NavigationRoute>,
        navigationTransitionScreenObject: JsonObject?,
    ) {
        emit(StackTransition.Event.PrepareTransition)
        coroutineScopes.navigation.await {
            // listen end of transition
            val transitionCompletion = coroutineScopes.navigation.async {
                events.filter { it == StackTransition.Event.TransitionComplete }.once(block = {
                    stackLock.withLock {
                        stack.retainAll { item ->
                            routes.any { it == item.route }
                        }
                    }
                    emit(StackTransition.Event.Idle)
                })
            }
            // update stack
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
                            transitionScreenObject = navigationTransitionScreenObject
                        )
                    )
                }
            }
            // build transition event
            val event: StackTransition.Event.RequestTransition = stackLock.withLock {
//                val directionNavigation = TransitionDirection.Navigation.Push
//                val foregroundRoutes = mutableListOf<NavigationRoute>()
//                var foregroundDirectionScreen: TransitionDirection.Screen
//                var foregroundTransitionSpec: JsonObject?
//                val backgroundRoutes = mutableListOf<NavigationRoute>()
//                var backgroundDirectionScreen: TransitionDirection.Screen
//                var backgroundTransitionSpec: JsonObject?
//
//                val pushedItem = stack.last()
//                val pushedTransition: JsonObject? = null //from push enter transition pushedItem
//                val pushedStackPreparationHelper = from(pushedTransition)
//                if (pushedStackPreparationHelper.isForeground(
//                        pushedTransition,
//                        TransitionDirection.Navigation.Push,
//                        TransitionDirection.Screen.Enter
//                    )
//                ) {
//                    foregroundRoutes.add(pushedItem.route)
//                    foregroundDirectionScreen = TransitionDirection.Screen.Enter
//                    foregroundTransitionSpec = pushedTransition
//
//                    val poppedItem = stack.dropLast(1).lastOrNull()
//                    poppedItem?.let {
//                        val poppedTransition: JsonObject? =
//                            null //from pop exit transition poppedItem
//                        backgroundRoutes.add(it.route)
//                        backgroundDirectionScreen = TransitionDirection.Screen.Exit
//                        backgroundTransitionSpec = poppedTransition
//                        if (!it.isBackgroundSolidSolid()) {
//                            stack.dropLast(2).forEach {
//                                if (it.isBackgroundSolidSolid()) {
//                                    break
//                                } else {
//                                    backgroundRoutes.add(it.route)
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    backgroundRoutes.add(pushedItem.route)
//                    backgroundDirectionScreen = TransitionDirection.Screen.Enter
//                    backgroundTransitionSpec = pushedTransition
//
//
//                    val poppedItem = stack.dropLast(1).lastOrNull()
//                    poppedItem?.let {
//                        val poppedTransition: JsonObject? =
//                            null //from pop exit transition poppedItem
//                        foregroundRoutes.add(it.route)
//                        foregroundDirectionScreen = TransitionDirection.Screen.Exit
//                        foregroundTransitionSpec = poppedTransition
//                        if (!it.isBackgroundSolidSolid()) {
//                            stack.dropLast(2).forEach {
//                                if (it.isBackgroundSolidSolid()) {
//                                    break
//                                } else {
//                                    foregroundRoutes.add(it.route)
//                                }
//                            }
//                        }
//                    }
//                }
                TODO()
            }
            emit(event)
            transitionCompletion.await()
        }

    }

    override suspend fun spit(routes: List<NavigationRoute>) {
        emit(StackTransition.Event.PrepareTransition)
        coroutineScopes.navigation.await {
            // listen end of transition
            val transitionCompletion = coroutineScopes.navigation.async {
                events.filter { it == StackTransition.Event.TransitionComplete }.once(block = {
                    stackLock.withLock {
                        stack.retainAll { item ->
                            routes.any { it == item.route }
                        }
                    }
                    emit(StackTransition.Event.Idle)
                })
            }
            // build transition event
            val event: StackTransition.Event.RequestTransition = stackLock.withLock {
//                val directionNavigation = TransitionDirection.Navigation.Pop
//                val foregroundRoutes = mutableListOf<NavigationRoute>()
//                var foregroundDirectionScreen: TransitionDirection.Screen
//                var foregroundTransitionSpec: JsonObject?
//                val backgroundRoutes = mutableListOf<NavigationRoute>()
//                var backgroundDirectionScreen: TransitionDirection.Screen
//                var backgroundTransitionSpec: JsonObject?
//
//                val poppedItem = stack.last()
//                val poppedTransition: JsonObject? = null //from popped enter transition poppedItem
//                val poppedStackPreparationHelper = from(poppedTransition)
//                if (poppedStackPreparationHelper.isForeground(
//                        poppedTransition,
//                        TransitionDirection.Navigation.Pop,
//                        TransitionDirection.Screen.Enter
//                    )
//                ) {
//                    foregroundRoutes.add(poppedItem.route)
//                    foregroundDirectionScreen = TransitionDirection.Screen.Enter
//                    foregroundTransitionSpec = poppedTransition
//
//                    val pushedItem = stack.dropLast(1).lastOrNull()
//                    pushedItem?.let {
//                        val pushedTransition: JsonObject? =
//                            null //from push exit transition poppedItem
//                        backgroundRoutes.add(it.route)
//                        backgroundDirectionScreen = TransitionDirection.Screen.Exit
//                        backgroundTransitionSpec = pushedTransition
//                        if (!it.isBackgroundSolidSolid()) {
//                            stack.dropLast(2).forEach {
//                                if (it.isBackgroundSolidSolid()) {
//                                    break
//                                } else {
//                                    backgroundRoutes.add(it.route)
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    backgroundRoutes.add(poppedItem.route)
//                    backgroundDirectionScreen = TransitionDirection.Screen.Enter
//                    backgroundTransitionSpec = poppedTransition
//
//
//                    val pushedItem = stack.dropLast(1).lastOrNull()
//                    pushedItem?.let {
//                        val pushedTransition: JsonObject? =
//                            null //from push exit transition poppedItem
//                        foregroundRoutes.add(it.route)
//                        foregroundDirectionScreen = TransitionDirection.Screen.Exit
//                        foregroundTransitionSpec = pushedTransition
//                        if (!it.isBackgroundSolidSolid()) {
//                            stack.dropLast(2).forEach {
//                                if (it.isBackgroundSolidSolid()) {
//                                    break
//                                } else {
//                                    foregroundRoutes.add(it.route)
//                                }
//                            }
//                        }
//                    }
//                }
                TODO()
            }
            emit(event)
            transitionCompletion.await()
        }
    }

}