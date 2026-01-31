package com.tezov.tuucho.core.domain.business.mock

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import kotlinx.coroutines.flow.FlowCollector

typealias ProcessActionMiddlewareTypeAlias = suspend FlowCollector<Unit>.(
    context: ActionMiddlewareProtocol.Context,
    next: MiddlewareProtocol.Next<ActionMiddlewareProtocol.Context, Unit>?
) -> Unit

class MockActionMiddleware(
    val command: String,
    var _accept: Boolean? = null,
    var _priority: Int? = null,
    var spy: SpyMiddlewareNext<ActionMiddlewareProtocol.Context>? = null
) : ActionMiddlewareProtocol {
    var process: ProcessActionMiddlewareTypeAlias = { context, _ ->
        spy?.invoke(
            context.copy(
                lockable = InteractionLockable.Empty,
                actionModel = ActionModel.from("$command://"),
                input = ProcessActionUseCase.Input(
                    route = null,
                    models = emptyList()
                )
            )
        )
    }

    override val priority: Int
        get() = _priority ?: error("mock priority not set")

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel
    ) = _accept ?: error("mock accept not set")

    override suspend fun FlowCollector<Unit>.process(
        context: ActionMiddlewareProtocol.Context,
        next: MiddlewareProtocol.Next<ActionMiddlewareProtocol.Context, Unit>?
    ) {
        process.invoke(this@process, context, next)
    }
}
