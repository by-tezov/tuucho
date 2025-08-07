package com.tezov.tuucho.core.domain.business.actionHandler

import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FormViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetOrNullScreenUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FormSendUrlActionHandler(
    private val useCaseExecutor: UseCaseExecutor,
    private val getOrNullScreen: GetOrNullScreenUseCase,
    private val sendData: SendDataUseCase,
) : ActionHandlerProtocol, KoinComponent {

    private val actionHandler: ActionHandlerUseCase by inject()

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(
        source: SourceIdentifierProtocol,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Form.command && action.authority == Action.Form.Send.authority
    }

    override suspend fun process(
        source: SourceIdentifierProtocol,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.target ?: return
        val formView = source.getAllFormView() ?: return
        if (formView.isAllFormValid()) {
            val response = useCaseExecutor.invokeSuspend(
                useCase = sendData,
                input = SendDataUseCase.Input(
                    url = action.target,
                    jsonObject = formView.data()
                )
            ).jsonObject
            response?.let {
                val responseScope = response.withScope(FormSendResponseSchema.Root::Scope)
                val isAllSuccess = responseScope.isAllSuccess == true
                if (isAllSuccess) {
                    jsonElement?.actionValidated(source)
                } else {
                    responseScope.processInvalidRemoteForm(source)
                }
            }
        } else {
            formView.processInvalidLocalForm(source)
        }

    }

    private suspend fun SourceIdentifierProtocol.getAllFormView() =
        useCaseExecutor.invokeSuspend(
            useCase = getOrNullScreen,
            input = GetOrNullScreenUseCase.Input(
                screenIdentifier = this
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

    private suspend fun List<FormViewProtocol>.processInvalidLocalForm(source: SourceIdentifierProtocol) {
        val results = filter { it.isValid() == false }
            .map {
                JsonNull.withScope(IdSchema::Scope).apply {
                    self = JsonNull.withScope(IdSchema::Scope)
                        .apply { value = it.getId() }.collect()
                }.collect()

            }.let(::JsonArray)
        actionDenied(source, results)
    }

    private suspend fun FormSendResponseSchema.Root.Scope.processInvalidRemoteForm(
        source: SourceIdentifierProtocol,
    ) {
        val results = results?.map { result ->
            val resultScope = result.withScope(FormSendResponseSchema.Result::Scope)
            val resultFailureReason = resultScope.failureReason
            JsonNull.withScope(::SchemaScope).apply {
                withScope(IdSchema::Scope).apply {
                    self = JsonNull.withScope(IdSchema::Scope).apply {
                        value = resultScope.id
                    }.collect()
                }
                resultFailureReason?.let {
                    withScope(FormSendResponseSchema.Result::Scope).apply {
                        failureReason = it
                    }
                }
            }.collect()
        }?.let(::JsonArray)
        actionDenied(source, results)
    }

    private suspend fun JsonElement.actionValidated(source: SourceIdentifierProtocol) {
        val actionValidated = withScope(FormSendResponseSchema.ActionParams::Scope).actionValidated
        actionValidated?.let {
            useCaseExecutor.invokeSuspend(
                useCase = actionHandler,
                input = ActionHandlerUseCase.Input(
                    source = source,
                    action = ActionModelDomain.from(it)
                ),
            )
        }
    }

    private suspend fun actionDenied(
        source: SourceIdentifierProtocol,
        results: JsonElement?,
    ) {
        useCaseExecutor.invokeSuspend(
            useCase = actionHandler,
            input = ActionHandlerUseCase.Input(
                source = source,
                action = ActionModelDomain.from(
                    command = Action.Form.command,
                    authority = Action.Form.Update.authority,
                    target = Action.Form.Update.Target.error
                ),
                paramElement = results
            ),
        )
    }

}