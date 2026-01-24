package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol.Companion.asHotFlow
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.tool.async.FlowMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class ActionExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val middlewares: List<ActionMiddleware>,
    private val interactionLockResolver: InteractionLockProtocol.Resolver,
    private val interactionLockRegistry: InteractionLockProtocol.Registry
) : ActionExecutorProtocol {
    override suspend fun process(
        input: Input
    ): Flow<Output>? {
        val flow = flow {
            input.models.forEach { actionModel ->
                val middlewaresToExecute = middlewares
                    .filter { it.accept(input.route, actionModel) }
                    .sortedByDescending { it.priority }
                middlewaresToExecute
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        val locks = input.lockable.acquireLocks(
                            route = input.route,
                            action = actionModel
                        )
                        middlewareExecutor.run {
                            process(
                                middlewares = it,
                                context = ActionMiddleware.Context(
                                    lockable = locks.freeze(),
                                    actionModel = actionModel,
                                    input = input,
                                )
                            )
                        }
                        locks.releaseLocks(
                            route = input.route,
                            action = actionModel
                        )
                    }
            }
        }.flowOn(coroutineScopes.action.context)
        return when (input.flowMode) {
            FlowMode.Hot -> flow.asHotFlow(coroutineScopes.action)
            FlowMode.Cold -> flow
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
