package com.tezov.tuucho.core.domain.business.interaction.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionFormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.TypeResponseSchema
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FormViewProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FormSendUrlActionProcessor(
    private val useCaseExecutor: UseCaseExecutor,
    private val getOrNullScreen: GetScreenOrNullUseCase,
    private val sendData: SendDataUseCase,
) : ActionProcessorProtocol, KoinComponent {

    private val actionHandler: ProcessActionUseCase by inject()

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return (action.command == Action.Form.command && action.authority == Action.Form.Send.authority)
    }

    override suspend fun process(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.target ?: return
        val formView = route.getAllFormView() ?: return
        if (formView.isAllFormValid()) {
            val response = useCaseExecutor.invokeSuspend(
                useCase = sendData,
                input = SendDataUseCase.Input(
                    url = action.target,
                    jsonObject = formView.data()
                )
            ).jsonObject
            response?.withScope(FormSendResponseSchema::Scope)
                ?.takeIf { it.type == TypeResponseSchema.Value.form }
                ?.run {
                    if (allSucceed.isTrue) {
                        processValidRemoteForm(route, jsonElement)
                    } else {
                        processInvalidRemoteForm(route, jsonElement)
                    }
                }
        } else {
            formView.processInvalidLocalForm(route)
        }
    }

    private suspend fun NavigationRoute.Url.getAllFormView() =
        useCaseExecutor.invokeSuspend(
            useCase = getOrNullScreen,
            input = GetScreenOrNullUseCase.Input(
                route = this
            )
        ).screen?.views(FormViewProtocol.Extension::class)?.map { it.formView }

    private fun List<FormViewProtocol>.isAllFormValid(): Boolean {
        forEach { it.updateValidity() }
        return all { it.isValid() ?: true }
    }

    private fun List<FormViewProtocol>.data() = buildMap<String, JsonPrimitive> {
        this@data.forEach {
            put(it.getId(), JsonPrimitive(it.getValue()))
        }
    }.let(::JsonObject)

    private suspend fun List<FormViewProtocol>.processInvalidLocalForm(
        route: NavigationRoute.Url,
    ) {
        val results = filter { it.isValid() == false }
            .map {
                JsonNull.withScope(IdSchema::Scope).apply {
                    self = JsonNull.withScope(IdSchema::Scope).apply {
                        value = it.getId()
                    }.collect()
                }.collect()
            }.let(::JsonArray)
        dispatchActionCommandError(route, results)
    }

    private suspend fun FormSendResponseSchema.Scope.processInvalidRemoteForm(
        route: NavigationRoute.Url,
        jsonElement: JsonElement?,
    ) {
        val responseCollect = collect()
        val responseActionScope = action?.withScope(FormSendResponseSchema.Action::Scope)
        responseActionScope?.before?.forEach {
            it.string.dispatchAction(route, responseCollect)
        }
        dispatchActionCommandError(route, toFailureResult())
        jsonElement?.withScope(ActionFormSchema.Send::Scope)
            ?.denied?.forEach {
                it.string.dispatchAction(route, responseCollect)
            }
        responseActionScope?.after?.forEach {
            it.string.dispatchAction(route, responseCollect)
        }
    }

    private suspend fun FormSendResponseSchema.Scope.processValidRemoteForm(
        route: NavigationRoute.Url,
        jsonElement: JsonElement?,
    ) {
        val responseCollect = collect()
        val responseActionScope = action?.withScope(FormSendResponseSchema.Action::Scope)
        responseActionScope?.before?.forEach {
            it.string.dispatchAction(route, responseCollect)
        }
        jsonElement?.withScope(ActionFormSchema.Send::Scope)
            ?.validated?.forEach {
                it.string.dispatchAction(route, responseCollect)
            }
        responseActionScope?.after?.forEach {
            it.string.dispatchAction(route, responseCollect)
        }
    }

    private fun FormSendResponseSchema.Scope.toFailureResult() = failureResult
        ?.jsonArray
        ?.map { result ->
            val scope = result.withScope(FormSendResponseSchema.FailureResult::Scope)
            JsonNull.withScope(::SchemaScope).apply {
                withScope(IdSchema::Scope).apply {
                    self = JsonNull.withScope(IdSchema::Scope).apply {
                        value = scope.id
                    }.collect()
                }
                scope.reason?.let {
                    withScope(FormSendResponseSchema.FailureResult::Scope).apply {
                        reason = it
                    }
                }
            }.collect()
        }?.let(::JsonArray)

    private suspend fun dispatchActionCommandError(
        route: NavigationRoute.Url,
        results: JsonElement?
    ) {
        useCaseExecutor.invokeSuspend(
            useCase = actionHandler,
            input = ProcessActionUseCase.Input(
                route = route,
                action = ActionModelDomain.from(
                    command = Action.Form.command,
                    authority = Action.Form.Update.authority,
                    target = Action.Form.Update.Target.error,
                ),
                jsonElement = results
            ),
        )
    }

    private suspend fun String.dispatchAction(
        route: NavigationRoute.Url,
        jsonElement: JsonElement?
    ) {
        useCaseExecutor.invokeSuspend(
            useCase = actionHandler,
            input = ProcessActionUseCase.Input(
                route = route,
                action = ActionModelDomain.from(this),
                jsonElement = jsonElement
            ),
        )
    }

}