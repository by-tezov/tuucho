package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val actionExecutor: ActionExecutorProtocol,
) : UseCaseProtocol.Async<Input, Output> {
    sealed class Input(
        val route: NavigationRoute.Url
    ) {
        class JsonElement(
            route: NavigationRoute.Url,
            val action: ActionModelDomain,
            val locks: List<Type>? = null,
            val jsonElement: kotlinx.serialization.json.JsonElement? = null,
        ) : Input(route)

        class ActionObject(
            route: NavigationRoute.Url,
            val actionObject: JsonObject,
        ) : Input(route)
    }

    sealed class Output(
        val type: KClass<Any>,
    ) {
        class Element(
            type: KClass<Any>,
            val rawValue: Any,
        ) : Output(type) {
            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> value(): T = rawValue as T

            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> valueOrNull(): T? = rawValue as? T
        }

        class ElementArray(
            type: KClass<Any>,
            val rawValue: List<Any>,
        ) : Output(type) {
            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> value(): List<T> = rawValue as List<T>

            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> valueOrNull(): List<T>? = rawValue as? List<T>
        }
    }

    override suspend fun invoke(
        input: Input
    ) = coroutineScopes.useCase.await {
        actionExecutor.process(input = input)
    }
}
