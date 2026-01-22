package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

@OpenForTest
class ProcessActionUseCase(
    private val actionExecutor: ActionExecutorProtocol,
) : UseCaseProtocol.Async<Input, Output> {
    sealed class Input {
        abstract val route: NavigationRoute?
        abstract val lockable: InteractionLockable?
        abstract val jsonElement: kotlinx.serialization.json.JsonElement?

        data class ActionModel(
            override val route: NavigationRoute?,
            val actionModel: com.tezov.tuucho.core.domain.business.model.action.ActionModel,
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
    ): Output? {
        val processorInputs = with(input) {
            when (this) {
                is Input.ActionObject -> {
                    buildList {
                        actionObject
                            .withScope(ActionSchema::Scope)
                            .primaries
                            ?.forEach { primary ->
                                add(
                                    Input.ActionModel(
                                        route = route,
                                        actionModel = ActionModel.from(primary.string),
                                        lockable = lockable,
                                        actionObjectOriginal = actionObject,
                                        jsonElement = jsonElement
                                    )
                                )
                            }
                    }
                }

                is Input.ActionModel -> {
                    listOf(this)
                }
            }
        }
        val outputs = processorInputs.mapNotNull {
            actionExecutor.process(input = it)
        }
        return when {
            outputs.isEmpty() -> {
                null
            }

            outputs.size == 1 -> {
                outputs.first()
            }

            else -> {
                Output.ElementArray(
                    values = outputs
                )
            }
        }
    }
}
