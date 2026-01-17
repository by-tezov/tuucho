package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

@OpenForTest
class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val actionExecutor: ActionExecutorProtocol,
) : UseCaseProtocol.Async<Input, Output> {

    sealed class Input {
        abstract val route: NavigationRoute?
        abstract val lockable: InteractionLockable?
        abstract val jsonElement: kotlinx.serialization.json.JsonElement?

        data class Action(
            override val route: NavigationRoute?,
            val action: ActionModelDomain,
            val actionObjectOriginal: JsonObject? = null,
            override val lockable: InteractionLockable? = null,
            override val jsonElement: kotlinx.serialization.json.JsonElement? = null,
        ) : Input()

        data class ActionObject(
            override val route: NavigationRoute?,
            val actionObject: JsonObject,
            override val lockable: InteractionLockable? = null,
            override val jsonElement: kotlinx.serialization.json.JsonElement? = null,
        ) : Input()
    }

    sealed class Output {
        class Element(
            val type: KClass<out Any>,
            val rawValue: Any,
        ) : Output() {
            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> value(): T = rawValue as T

            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> valueOrNull(): T? = rawValue as? T
        }

        class ElementArray(
            val values: List<Output>,
        ) : Output()
    }

    override suspend fun invoke(
        input: Input
    ) = coroutineScopes.useCase.await {
        actionExecutor.process(input = input)
    }
}
