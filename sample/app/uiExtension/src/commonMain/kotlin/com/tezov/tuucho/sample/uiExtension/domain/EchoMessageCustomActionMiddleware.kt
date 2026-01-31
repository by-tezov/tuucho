package com.tezov.tuucho.sample.uiExtension.domain

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Message
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.json.JsonNull

internal class EchoMessageCustomActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val updateView: UpdateViewUseCase,
) : ActionMiddleware,
    TuuchoKoinComponent {

    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ): Boolean = action.command == EchoMessageCustomActionDefinition.command

    override suspend fun ProducerScope<Unit>.process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, Unit>?
    ) {
        val route = context.input.route ?: run {
            next.invoke(context)
            return
        }
        val jsonElement = context.input.jsonElement ?: run {
            next.invoke(context)
            return
        }
        val messageScope = jsonElement.withScope(Message::Scope)
        val messages = JsonNull
            .withScope(Message::Scope)
            .apply {
                id = messageScope.id
                type = TypeSchema.Value.message
                subset = Message.Value.Subset.customLabelMessage
                downstream = messageScope.upstream?.let { it + 1 }
            }.collect()
        useCaseExecutor.await(
            useCase = updateView,
            input = UpdateViewUseCase.Input(
                route = route,
                jsonObjects = listOf(messages)
            )
        )
        next.invoke(context)
    }
}
