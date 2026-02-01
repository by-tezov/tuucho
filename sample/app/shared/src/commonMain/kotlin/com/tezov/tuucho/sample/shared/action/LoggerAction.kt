package com.tezov.tuucho.sample.shared.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerAction(
    private val logger: Logger
) : ActionMiddlewareProtocol {
    override val priority: Int = ActionMiddlewareProtocol.Priority.LOW

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = true

    override suspend fun process(
        context: ActionMiddlewareProtocol.Context,
        next: MiddlewareProtocol.Next<ActionMiddlewareProtocol.Context>?
    ) {
        logger.thread()
        logger.debug("ACTION") { "from ${context.input.route}: ${context.actionModel}" }
        next?.invoke(context)
    }
}
