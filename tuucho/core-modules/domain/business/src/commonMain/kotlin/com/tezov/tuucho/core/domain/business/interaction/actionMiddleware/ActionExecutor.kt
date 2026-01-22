package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output

internal class ActionExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val middlewares: List<ActionMiddleware>,
    private val interactionLockResolver: InteractionLockProtocol.Resolver,
    private val interactionLockRegistry: InteractionLockProtocol.Registry
) : ActionExecutorProtocol {
    override suspend fun process(
        input: Input.ActionModel
    ) = with(input) {
        coroutineScopes.action.await {
            val middlewaresToExecute = middlewares
                .filter { it.accept(route, actionModel) }
                .sortedByDescending { it.priority }
            middlewaresToExecute
                .takeIf { it.isNotEmpty() }
                ?.let {
                    val locks = input.lockable.acquireLocks(
                        route = route,
                        action = actionModel
                    )
                    val result: Output.ElementArray? = middlewareExecutor.process(
                        middlewares = it,
                        context = ActionMiddleware.Context(
                            lockable = locks.freeze(),
                            input = input,
                        )
                    )
                    locks.releaseLocks(
                        route = route,
                        action = actionModel
                    )
                    result
                }
        }
    }

    private suspend fun InteractionLockable?.acquireLocks(
        route: NavigationRoute?,
        action: ActionModel,
    ): InteractionLockable {
        val lockTypesForCommand = interactionLockRegistry.lockTypeFor(
            command = action.command,
            authority = action.authority
        )
        val lockToAcquire = lockTypesForCommand + (this ?: InteractionLockable.Empty)
        val acquiredLocks = interactionLockResolver.acquire(
            requester = "$route::$action",
            lockable = lockToAcquire
        )
        return acquiredLocks
    }

    private suspend fun InteractionLockable.releaseLocks(
        route: NavigationRoute?,
        action: ActionModel,
    ) {
        interactionLockResolver.release(
            requester = "$route::$action",
            lockable = this
        )
    }
}
