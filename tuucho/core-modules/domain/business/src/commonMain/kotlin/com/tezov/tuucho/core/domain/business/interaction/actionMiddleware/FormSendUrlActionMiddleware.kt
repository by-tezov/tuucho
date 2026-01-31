package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionFormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.FormActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.put
import org.koin.core.component.inject

internal class FormSendUrlActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val getScreenOrNull: GetScreenOrNullUseCase,
    private val sendData: SendDataUseCase,
) : ActionMiddlewareProtocol,
    TuuchoKoinComponent {
    private val processAction: ProcessActionUseCase by inject()

    override val priority: Int
        get() = ActionMiddlewareProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == FormActionDefinition.Send.command &&
        action.authority == FormActionDefinition.Send.authority &&
        action.target != null

    override suspend fun ProducerScope<Unit>.process(
        context: Context,
        next: MiddlewareProtocol.Next<Context, Unit>?
    ) {
        val formView = (context.input.route as? NavigationRoute.Url)?.getAllFormView() ?: run {
            next.invoke(context)
            return
        }
        val route = context.input.route
        if (formView.isAllFormValid()) {
            val response = useCaseExecutor
                .await(
                    useCase = sendData,
                    input = SendDataUseCase.Input(
                        url = context.actionModel.target ?: throw DomainException.Default("should no be possible"),
                        jsonObject = formView.data()
                    )
                )?.jsonObject
            response
                ?.withScope(FormSendSchema::Scope)
                ?.takeIf { it.subset == FormSendSchema.Value.subset }
                ?.run {
                    if (allSucceed.isTrue) {
                        processValidRemoteForm(route, context.lockable, context.input.modelObjectOriginal)
                    } else {
                        processInvalidRemoteForm(route, context.lockable, context.input.modelObjectOriginal)
                    }
                }
        } else {
            formView.processInvalidLocalForm(route)
        }
        next.invoke(context)
    }

    private suspend fun NavigationRoute.Url.getAllFormView() = useCaseExecutor
        .await(
            useCase = getScreenOrNull,
            input = GetScreenOrNullUseCase.Input(route = this)
        )?.screen
        ?.views(FormStateProtocol.Extension::class)
        ?.map { it.extensionFormState }

    private fun List<FormStateProtocol>.isAllFormValid(): Boolean {
        forEach { it.updateValidity() }
        return all { it.isValid() ?: true }
    }

    private fun List<FormStateProtocol>.data() = buildJsonObject {
        forEach {
            put(it.getId(), it.getValue())
        }
    }

    private suspend fun List<FormStateProtocol>.processInvalidLocalForm(
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

    private suspend fun FormSendSchema.Scope.processInvalidRemoteForm(
        route: NavigationRoute.Url,
        lockable: InteractionLockable,
        actionObject: JsonElement?,
    ) {
        val responseCollect = collect()
        val responseActionScope = action?.withScope(FormSendSchema.Action::Scope)
        responseActionScope?.before?.forEach {
            it.string.dispatchAction(route, lockable, responseCollect)
        }
        dispatchActionCommandError(route, toFailureResult())
        actionObject
            ?.withScope(ActionFormSchema.Send::Scope)
            ?.denied
            ?.forEach {
                it.string.dispatchAction(route, lockable, responseCollect)
            }
        responseActionScope?.after?.forEach {
            it.string.dispatchAction(route, lockable, responseCollect)
        }
    }

    private suspend fun FormSendSchema.Scope.processValidRemoteForm(
        route: NavigationRoute.Url,
        lockable: InteractionLockable,
        actionObject: JsonElement?,
    ) {
        val responseCollect = collect()
        val responseActionScope = action?.withScope(FormSendSchema.Action::Scope)
        responseActionScope?.before?.forEach {
            it.string.dispatchAction(route, lockable, responseCollect)
        }
        actionObject
            ?.withScope(ActionFormSchema.Send::Scope)
            ?.validated
            ?.forEach {
                it.string.dispatchAction(route, lockable, responseCollect)
            }
        responseActionScope?.after?.forEach {
            it.string.dispatchAction(route, lockable, responseCollect)
        }
    }

    private fun FormSendSchema.Scope.toFailureResult() = failureResults
        ?.jsonArray
        ?.map { result ->
            val scope = result.withScope(FormSendSchema.FailureResult::Scope)
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
                        withScope(FormSendSchema.FailureResult::Scope).apply {
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
            useCase = processAction,
            input = ProcessActionUseCase.Input.create(
                route = route,
                model = ActionModel.from(
                    command = FormActionDefinition.Update.command,
                    authority = FormActionDefinition.Update.authority,
                    target = FormActionDefinition.Update.Target.error,
                ),
                jsonElement = results
            )
        )
    }

    private suspend fun String.dispatchAction(
        route: NavigationRoute.Url,
        lockable: InteractionLockable,
        response: JsonElement?
    ) {
        useCaseExecutor.await(
            useCase = processAction,
            input = ProcessActionUseCase.Input.create(
                route = route,
                model = ActionModel.from(this),
                jsonElement = response,
                lockable = lockable
            )
        )
    }
}
