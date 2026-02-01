package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input

internal class ActionExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val middlewares: List<ActionMiddlewareProtocol>,
    private val interactionLockResolver: InteractionLockProtocol.Resolver,
    private val interactionLockRegistry: InteractionLockProtocol.Registry
) : ActionExecutorProtocol {
    override suspend fun process(
        input: Input
    ) {
        coroutineScopes.default.withContext {
            input.models.forEach { actionModel ->
                val middlewaresToExecute = middlewares
                    .filter { it.accept(input.route, actionModel) }
                    .sortedByDescending { it.priority }
                middlewaresToExecute
                    .takeIf { it.isNotEmpty() }
                    ?.run {
                        val locks = input.lockable.acquireLocks(
                            route = input.route,
                            action = actionModel
                        )
                        middlewareExecutor.process(
                            middlewares = this,
                            context = ActionMiddlewareProtocol.Context(
                                lockable = locks.freeze(),
                                actionModel = actionModel,
                                input = input,
                            )
                        )
                        locks.releaseLocks(
                            route = input.route,
                            action = actionModel
                        )
                    }
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
