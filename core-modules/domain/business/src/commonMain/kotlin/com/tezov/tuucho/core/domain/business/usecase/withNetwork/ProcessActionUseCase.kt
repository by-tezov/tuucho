package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val actionExecutor: ActionExecutorProtocol,
) : UseCaseProtocol.Async<Input, Output> {
    sealed class Input {
        abstract val route: NavigationRoute.Url?
        abstract val lockable: InteractionLockable?

        data class JsonElement(
            override val route: NavigationRoute.Url?,
            val action: ActionModelDomain,
            override val lockable: InteractionLockable? = null,
            val jsonElement: kotlinx.serialization.json.JsonElement? = null,
        ) : Input()

        data class ActionObject(
            override val route: NavigationRoute.Url?,
            val actionObject: JsonObject,
            override val lockable: InteractionLockable? = null,
        ) : Input()
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
