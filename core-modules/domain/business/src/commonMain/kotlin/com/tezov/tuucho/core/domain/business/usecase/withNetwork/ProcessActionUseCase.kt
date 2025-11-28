package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val actionExecutor: ActionExecutorProtocol,
    private val getScreenOrNull: GetScreenOrNullUseCase,
) : UseCaseProtocol.Async<Input, Output> {
    sealed class Input {
        abstract val route: NavigationRoute?
        abstract val lockable: InteractionLockable?

        data class JsonElement(
            override val route: NavigationRoute?,
            val action: ActionModelDomain,
            override val lockable: InteractionLockable? = null,
            val jsonElement: kotlinx.serialization.json.JsonElement? = null,
        ) : Input()

        data class ActionObject(
            override val route: NavigationRoute?,
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
//        val _input = if (input.route is NavigationRoute.Current) {
//            val currentRoute = getScreenOrNull.invoke(
//                GetScreenOrNullUseCase.Input(
//                    route = input.route
//                )
//            ).screen?.route
//            input.copy(route = NavigationRoute.Current)
//        } else {
//            input
//        }
        actionExecutor.process(input = input)
    }
}
