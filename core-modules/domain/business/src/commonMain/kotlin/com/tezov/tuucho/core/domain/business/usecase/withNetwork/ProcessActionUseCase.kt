package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Companion.execute
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val actionMiddlewares: List<ActionMiddleware>,
) : UseCaseProtocol.Async<Input, Output> {
    data class Input(
        val route: NavigationRoute.Url,
        val action: ActionModelDomain,
        val jsonElement: JsonElement? = null,
    )

    data class Output(
        val type: KClass<Any>,
        val rawValue: Any?
    ) {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> value(): T = rawValue as T

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> valueOrNull(): T? = rawValue as? T
    }

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.useCase.await {
            val middlewaresToExecute = actionMiddlewares
                .filter { it.accept(route, action) }
                .sortedBy { it.priority }
            middlewaresToExecute.execute(
                context = ActionMiddleware.Context(
                    input = input,
                )
            )
        }
    }
}
