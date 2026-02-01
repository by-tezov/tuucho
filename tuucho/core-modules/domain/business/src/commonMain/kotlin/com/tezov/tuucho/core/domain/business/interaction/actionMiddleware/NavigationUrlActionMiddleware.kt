package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase

internal class NavigationUrlActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigateToUrl: NavigateToUrlUseCase,
) : ActionMiddlewareProtocol {
    override val priority: Int
        get() = ActionMiddlewareProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == NavigateActionDefinition.Url.command &&
        action.authority == NavigateActionDefinition.Url.authority

    override suspend fun process(
        context: Context,
        next: MiddlewareProtocol.Next<Context>?
    ) {
        context.actionModel.target?.let { url ->
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(
                    url = url,
                )
            )
        }
        next?.invoke(context)
    }
}
