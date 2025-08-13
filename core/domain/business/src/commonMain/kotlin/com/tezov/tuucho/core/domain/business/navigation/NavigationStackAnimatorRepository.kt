package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackAnimator
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class NavigationStackAnimatorRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : StackAnimator, KoinComponent {

    data class Item(
        val route: NavigationRoute,
//        val animation: NavigationAnimationType,
        val requestToRemove: Boolean,
    )

    private val _animate = Notifier.Emitter<Boolean>()
    override val animate get() = _animate.createCollector

    private var stack = emptyList<Item>()

    override suspend fun routes() = coroutineScopes.navigation.await {
        stack.map { it.route }
    }

    override suspend fun getVisibleRoutes(): List<NavigationRoute> {
        return stack.map { it.route }
    }

    override suspend fun notifyTransitionCompleted() {
        coroutineScopes.navigation.async { _animate.emit(false) }
    }

    override suspend fun swallow(
        routes: List<NavigationRoute>,
        animationObject: JsonObject,
    ) {
        coroutineScopes.navigation.await {
            stack = routes.map { Item(route = it, requestToRemove = false) } //TODO
            val transitionCompletion = coroutineScopes.navigation.async {
                animate.filter { !it }.once(block = {
                    stack = stack.filter { !it.requestToRemove }
                })
            }
            _animate.emit(true)
            transitionCompletion.await()

        }
    }

    override suspend fun spit(routes: List<NavigationRoute>) {
        coroutineScopes.navigation.await {
            stack = routes.map { Item(route = it, requestToRemove = false) } //TODO
            val transitionCompletion = coroutineScopes.navigation.async {
                animate.filter { !it }.once(block = {
                    stack = stack.filter { !it.requestToRemove }
                })
            }
            _animate.emit(true)
            transitionCompletion.await()
        }
    }

}