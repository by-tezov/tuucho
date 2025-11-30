package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.tool.json.string

internal class ActionExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val middlewares: List<ActionMiddleware>,
    private val interactionLockResolver: InteractionLockProtocol.Resolver,
    private val interactionLockRegistry: InteractionLockProtocol.Registry
) : ActionExecutorProtocol {
    override suspend fun process(
        input: Input
    ) = with(input) {
        when (this) {
            is Input.ActionObject -> {
                val outputs: List<Output> = buildList {
                    actionObject
                        .withScope(ActionSchema::Scope)
                        .primary
                        ?.forEach { jsonElement ->
                            val output = process(
                                Input.JsonElement(
                                    route = route,
                                    action = ActionModelDomain.from(jsonElement.string),
                                    lockable = lockable,
                                    jsonElement = actionObject
                                )
                            )
                            output?.let { add(it) }
                        }
                }
                when {
                    outputs.isEmpty() -> {
                        null
                    }

                    outputs.size == 1 -> {
                        Output.Element(
                            type = Any::class,
                            rawValue = outputs.first()
                        )
                    }

                    else -> {
                        Output.ElementArray(
                            type = Any::class,
                            rawValue = outputs
                        )
                    }
                }
            }

            is Input.JsonElement -> {
                process(this)
            }
        }
    }

    private suspend fun process(
        input: Input.JsonElement
    ) = with(input) {
        coroutineScopes.action.await {
            val middlewaresToExecute = middlewares
                .filter { it.accept(route, action) }
                .sortedBy { it.priority }
            if (middlewaresToExecute.isNotEmpty()) {
                val locks = input.lockable.acquireLocks(
                    route = route,
                    action = action
                )
                val result = middlewareExecutor.process(
                    middlewaresToExecute,
                    ActionMiddleware.Context(
                        lockable = locks.freeze(),
                        input = input,
                    )
                )
                locks.releaseLocks(
                    route = route,
                    action = action
                )
                result
            } else {
                null
            }
        }
    }

    private suspend fun InteractionLockable?.acquireLocks(
        route: NavigationRoute?,
        action: ActionModelDomain,
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
        action: ActionModelDomain,
    ) {
        interactionLockResolver.release(
            requester = "$route::$action",
            lockable = this
        )
    }
}
