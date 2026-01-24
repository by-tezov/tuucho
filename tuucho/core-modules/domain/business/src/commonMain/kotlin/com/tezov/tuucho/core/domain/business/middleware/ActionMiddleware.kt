package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware.Context
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import kotlinx.coroutines.channels.ProducerScope

interface ActionMiddleware : MiddlewareProtocol<Context, Unit> {
    data class Context(
        val flowProducer: ProducerScope<ProcessActionUseCase.Output>,
        val lockable: InteractionLockable,
        val actionModel: ActionModel,
        val input: ProcessActionUseCase.Input
    )

    object Priority {
        const val LOW = 0
        const val DEFAULT = 100
        const val HIGH = 200
    }

    val priority: Int

    fun accept(
        route: NavigationRoute?,
        action: ActionModel
    ): Boolean
}
