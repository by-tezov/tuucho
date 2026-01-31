package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.FormActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray

internal class FormUpdateActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val updateView: UpdateViewUseCase,
) : ActionMiddlewareProtocol,
    TuuchoKoinComponent {
    override val priority: Int
        get() = ActionMiddlewareProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ): Boolean = action.command == FormActionDefinition.Update.command &&
        action.authority == FormActionDefinition.Update.authority

    override suspend fun ProducerScope<Unit>.process(
        context: Context,
        next: MiddlewareProtocol.Next<Context, Unit>?
    ) {
        val route = context.input.route ?: run {
            next.invoke(context)
            return
        }
        when (val target = context.actionModel.target) {
            FormActionDefinition.Update.Target.error -> updateErrorState(route, context.input.jsonElement)
            else -> throw DomainException.Default("Unknown target $target")
        }
        next.invoke(context)
    }

    private suspend fun updateErrorState(
        route: NavigationRoute,
        jsonElement: JsonElement?,
    ) {
        val messages = jsonElement?.jsonArray?.map { param ->
            JsonNull
                .withScope(FormSchema.Message::Scope)
                .apply {
                    id = param.withScope(IdSchema::Scope).self
                    type = TypeSchema.Value.message
                    subset = FormSchema.Message.Value.Subset.updateErrorState
                    param.withScope(FormSendSchema.FailureResult::Scope).reason?.let {
                        messageErrorExtra = it
                    }
                }.collect()
        }
        messages?.let {
            useCaseExecutor.await(
                useCase = updateView,
                input = UpdateViewUseCase.Input(
                    route = route,
                    jsonObjects = messages
                )
            )
        }
    }
}
