package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware.Context
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Provider
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

interface ActionMiddleware : MiddlewareProtocol<Context, ProcessActionUseCase.Output?> {
    data class Context(
        val lockProvider: Provider,
        val input: ProcessActionUseCase.Input.JsonElement
    )

    object Priority {
        const val LOW = 0
        const val DEFAULT = 100
        const val HIGH = 200
    }

    val priority: Int

    fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain
    ): Boolean
}
