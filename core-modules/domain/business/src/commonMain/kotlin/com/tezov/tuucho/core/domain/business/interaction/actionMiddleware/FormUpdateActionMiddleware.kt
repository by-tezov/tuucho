package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.FormAction
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray

internal class FormUpdateActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val updateView: UpdateViewUseCase,
) : ActionMiddleware,
    TuuchoKoinComponent {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
    ): Boolean = action.command == FormAction.command && action.authority == FormAction.Update.authority

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        when (val target = action.target) {
            FormAction.Update.Target.error -> updateErrorState(route, jsonElement)
            else -> throw DomainException.Default("Unknown target $target")
        }
        next.invoke(context)
    }

    private suspend fun updateErrorState(
        route: NavigationRoute.Url,
        jsonElement: JsonElement?,
    ) {
        jsonElement?.jsonArray?.forEach { param ->
            val message = JsonNull
                .withScope(FormSchema.Message::Scope)
                .apply {
                    id = param.withScope(IdSchema::Scope).self
                    type = TypeSchema.Value.message
                    subset = FormSchema.Message.Value.Subset.updateErrorState
                    param.withScope(FormSendResponseSchema.FailureResult::Scope).reason?.let {
                        messageErrorExtra = it
                    }
                }.collect()
            useCaseExecutor.await(
                useCase = updateView,
                input = UpdateViewUseCase.Input(
                    route = route,
                    jsonObject = message
                )
            )
        }
    }
}
