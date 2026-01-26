package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@OpenForTest
class ProcessActionUseCase(
    private val actionExecutor: ActionExecutorProtocol,
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val route: NavigationRoute?,
        val models: List<ActionModel>,
        val modelObjectOriginal: JsonElement? = null,
        val lockable: InteractionLockable? = null,
        val jsonElement: JsonElement? = null
    ) {
        companion object {
            fun create(
                route: NavigationRoute?,
                modelObject: JsonObject,
                lockable: InteractionLockable? = null,
                jsonElement: JsonElement? = null,
            ) = buildList {
                modelObject
                    .withScope(ActionSchema::Scope)
                    .primaries
                    ?.map { ActionModel.from(it.string) }
                    ?.let(::addAll)
            }.let {
                Input(
                    route = route,
                    models = it,
                    modelObjectOriginal = modelObject,
                    lockable = lockable,
                    jsonElement = jsonElement,
                )
            }

            fun create(
                route: NavigationRoute?,
                models: List<ActionModel>,
                lockable: InteractionLockable? = null,
                jsonElement: JsonElement? = null,
            ) = Input(
                route = route,
                models = models,
                lockable = lockable,
                jsonElement = jsonElement,
            )

            fun create(
                route: NavigationRoute?,
                model: ActionModel,
                lockable: InteractionLockable? = null,
                jsonElement: JsonElement? = null,
            ) = Input(
                route = route,
                models = listOf(model),
                lockable = lockable,
                jsonElement = jsonElement,
            )
        }
    }

    override suspend fun invoke(
        input: Input
    ) {
        actionExecutor.process(input = input)
    }
}
