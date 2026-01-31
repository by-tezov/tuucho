package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol.Context
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

interface ActionMiddlewareProtocol : MiddlewareProtocol<Context, Unit> {
    data class Context(
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
