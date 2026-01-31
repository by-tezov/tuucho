package com.tezov.tuucho.sample.shared.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector

class LoggerAction(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : ActionMiddlewareProtocol {
    override val priority: Int = ActionMiddlewareProtocol.Priority.LOW

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = true

    override suspend fun ProducerScope<Unit>.process(
        context: ActionMiddlewareProtocol.Context,
        next: MiddlewareProtocol.Next<ActionMiddlewareProtocol.Context, Unit>?
    ) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("ACTION") { "from ${context.input.route}: ${context.actionModel}" }
        next.invoke(context)
    }
}
