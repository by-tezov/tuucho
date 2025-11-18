package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Companion.execute
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.coroutines.CompletableDeferred

internal class ActionExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewares: List<ActionMiddleware>,
    private val interactionLockRepository: InteractionLockRepositoryProtocol,
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
                                    locks = emptyList(),
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
        val middlewaresToExecute = middlewares
            .filter { it.accept(route, action) }
            .sortedBy { it.priority }
        val deferred = CompletableDeferred<Output?>()
        coroutineScopes.action.async {
            val acquiredLocks = if (input.locks?.isNotEmpty() == true) {
                interactionLockRepository.acquire(input.locks)
            } else {
                null
            }
            val result = middlewaresToExecute.execute(
                ActionMiddleware.Context(input)
            )
            deferred.complete(result)
            acquiredLocks?.let {
                interactionLockRepository.release(it)
            }
        }
        deferred.await()
    }
}
