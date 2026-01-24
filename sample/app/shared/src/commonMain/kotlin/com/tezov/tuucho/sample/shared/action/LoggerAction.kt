package com.tezov.tuucho.sample.shared.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.flow.FlowCollector

class LoggerAction(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : ActionMiddleware {
    override val priority: Int = ActionMiddleware.Priority.LOW

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = true

    override suspend fun FlowCollector<Output>.process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, Output>?
    ) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("ACTION") { "from ${context.input.route}: ${context.actionModel}" }
        next?.invoke(context)
    }
}
