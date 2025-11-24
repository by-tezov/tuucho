package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.NavigateAction
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

internal class NavigationUrlActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigateToUrl: NavigateToUrlUseCase,
) : ActionMiddleware {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url?,
        action: ActionModelDomain,
    ): Boolean = action.command == NavigateAction.command && action.authority == NavigateAction.Url.authority

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        action.target?.let { url ->
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(
                    url = url,
                )
            )
        }
        next.invoke(context)
    }
}
