package com.tezov.tuucho.shared.sample.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerAction(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : ActionMiddleware {
    override val priority: Int = ActionMiddleware.Priority.LOW

    override fun accept(
        route: NavigationRoute.Url?,
        action: ActionModelDomain,
    ) = true

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("ACTION") { "from ${route?.value}: $action" }
        next.invoke(context)
    }
}
