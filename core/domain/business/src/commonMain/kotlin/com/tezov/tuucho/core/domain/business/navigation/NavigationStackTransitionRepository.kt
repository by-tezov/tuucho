package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationScreen
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationType
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting.AnimationSetting
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonArray
import org.koin.core.component.KoinComponent

class NavigationStackTransitionRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : StackTransition, KoinComponent {

    private var lastEvent: StackTransition.Event = StackTransition.Event.Idle
    private val _events = Notifier.Emitter<StackTransition.Event>()
    override val events get() = _events.createCollector

    private var stack = mutableListOf<StackTransition.Item>()
    private val stackLock = Mutex()

    override suspend fun isBusy() = coroutineScopes.event.await {
        stackLock.withLock { lastEvent != StackTransition.Event.Idle }
    }

    override suspend fun routes() = coroutineScopes.navigation.await {
        stackLock.withLock { stack.map { it.route } }
    }

    override suspend fun getRoutesWithAnimationOptions(): List<StackTransition.Item> {
        return stackLock.withLock { stack.toList() }
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
        animationOptionObject: JsonArray?,
    ) {
        coroutineScopes.navigation.await {
            stackLock.withLock {
                // assume, only existing one was moved at tail or new one added at tail
                val tailRoute = routes.last()
                val existingIndex = stack.indexOfFirst { it.route == tailRoute}
                if (existingIndex >= 0) {
                    val route = stack.removeAt(existingIndex)
                    stack.add(route)
                } else {
                    stack.add(StackTransition.Item(
                        route = tailRoute,
                        animationScreen = AnimationScreen( //TODO
                            enter = AnimationType(
                                push = AnimationSetting.Fade(),
                                pop = AnimationSetting.Fade()
                            ),
                            exit = AnimationType(
                                push = AnimationSetting.Fade(),
                                pop = AnimationSetting.Fade()
                            )
                        )
                    ))
                }
            }
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
            emit(StackTransition.Event.RequestTransitionForward)
            transitionCompletion.await()
        }
    }

    override suspend fun spit(routes: List<NavigationRoute>) {
        coroutineScopes.navigation.await {
            stackLock.withLock {
                // assume, only the one at tail was removed
                val tailRoute = routes.last()
                val existingIndex = stack.indexOfFirst { it.route == tailRoute}
                if (existingIndex >= 0) {
                    val route = stack.removeAt(existingIndex)
                    stack.add(route)
                } else {
                    stack.add(StackTransition.Item(
                        route = tailRoute,
                        animationScreen = AnimationScreen( //TODO
                            enter = AnimationType(
                                push = AnimationSetting.Fade(),
                                pop = AnimationSetting.Fade()
                            ),
                            exit = AnimationType(
                                push = AnimationSetting.Fade(),
                                pop = AnimationSetting.Fade()
                            )
                        )
                    ))
                }
            }
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
            emit(StackTransition.Event.RequestTransitionForward)
            transitionCompletion.await()
        }
    }

}