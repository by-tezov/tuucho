package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionFormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.TypeResponseSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.FormAction
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Provider
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FormViewProtocol
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
import org.koin.core.component.inject

internal class FormSendUrlActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val getOrNullScreen: GetScreenOrNullUseCase,
    private val sendData: SendDataUseCase,
) : ActionMiddleware,
    TuuchoKoinComponent {
    private val actionHandler: ProcessActionUseCase by inject()

    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
    ): Boolean = (action.command == FormAction.command && action.authority == FormAction.Send.authority && action.target != null)

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        val formView = route.getAllFormView() ?: return@with next.invoke(context)
        if (formView.isAllFormValid()) {
            val response = useCaseExecutor
                .await(
                    useCase = sendData,
                    input = SendDataUseCase.Input(
                        url = action.target ?: throw DomainException.Default("should no be possible"),
                        jsonObject = formView.data()
                    )
                )?.jsonObject
            response
                ?.withScope(FormSendResponseSchema::Scope)
                ?.takeIf { it.type == TypeResponseSchema.Value.form }
                ?.run {
                    if (allSucceed.isTrue) {
                        processValidRemoteForm(route, context.lockProvider, jsonElement)
                    } else {
                        processInvalidRemoteForm(route, context.lockProvider, jsonElement)
                    }
                }
        } else {
            formView.processInvalidLocalForm(route)
        }
        next.invoke(context)
    }

    private suspend fun NavigationRoute.Url.getAllFormView() = useCaseExecutor
        .await(
            useCase = getOrNullScreen,
            input = GetScreenOrNullUseCase.Input(
                route = this
            )
        )?.screen
        ?.views(FormViewProtocol.Extension::class)
        ?.map { it.formView }

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
                JsonNull
                    .withScope(IdSchema::Scope)
                    .apply {
                        self = JsonNull
                            .withScope(IdSchema::Scope)
                            .apply {
                                value = it.getId()
                            }.collect()
                    }.collect()
            }.let(::JsonArray)
        dispatchActionCommandError(route, results)
    }

    private suspend fun FormSendResponseSchema.Scope.processInvalidRemoteForm(
        route: NavigationRoute.Url,
        lockProvider: Provider,
        jsonElement: JsonElement?,
    ) {
        val responseCollect = collect()
        val responseActionScope = action?.withScope(FormSendResponseSchema.Action::Scope)
        responseActionScope?.before?.forEach {
            it.string.dispatchAction(route, lockProvider, responseCollect)
        }
        dispatchActionCommandError(route, toFailureResult())
        jsonElement
            ?.withScope(ActionFormSchema.Send::Scope)
            ?.denied
            ?.forEach {
                it.string.dispatchAction(route, lockProvider, responseCollect)
            }
        responseActionScope?.after?.forEach {
            it.string.dispatchAction(route, lockProvider, responseCollect)
        }
    }

    private suspend fun FormSendResponseSchema.Scope.processValidRemoteForm(
        route: NavigationRoute.Url,
        lockProvider: Provider,
        jsonElement: JsonElement?,
    ) {
        val responseCollect = collect()
        val responseActionScope = action?.withScope(FormSendResponseSchema.Action::Scope)
        responseActionScope?.before?.forEach {
            it.string.dispatchAction(route, lockProvider, responseCollect)
        }
        jsonElement
            ?.withScope(ActionFormSchema.Send::Scope)
            ?.validated
            ?.forEach {
                it.string.dispatchAction(route, lockProvider, responseCollect)
            }
        responseActionScope?.after?.forEach {
            it.string.dispatchAction(route, lockProvider, responseCollect)
        }
    }

    private fun FormSendResponseSchema.Scope.toFailureResult() = failureResult
        ?.jsonArray
        ?.map { result ->
            val scope = result.withScope(FormSendResponseSchema.FailureResult::Scope)
            JsonNull
                .withScope(::SchemaScope)
                .apply {
                    withScope(IdSchema::Scope).apply {
                        self = JsonNull
                            .withScope(IdSchema::Scope)
                            .apply {
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
        useCaseExecutor.await(
            useCase = actionHandler,
            input = ProcessActionUseCase.Input.JsonElement(
                route = route,
                action = ActionModelDomain.from(
                    command = FormAction.command,
                    authority = FormAction.Update.authority,
                    target = FormAction.Update.Target.error,
                ),
                jsonElement = results
            ),
        )
    }

    private suspend fun String.dispatchAction(
        route: NavigationRoute.Url,
        lockProvider: Provider,
        jsonElement: JsonElement?
    ) {
        useCaseExecutor.await(
            useCase = actionHandler,
            input = ProcessActionUseCase.Input.JsonElement(
                route = route,
                action = ActionModelDomain.from(this),
                jsonElement = jsonElement,
                lockProvider = lockProvider
            ),
        )
    }
}
